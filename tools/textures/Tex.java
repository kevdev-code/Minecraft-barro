import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * Texture generator for Barro. Run from the repository root:
 *
 *   java tools/textures/Tex.java                                  regenerate every texture from its spec
 *   java tools/textures/Tex.java preview <out> <scale> <tiles> <png...>   upscaled side-by-side preview, each tiled NxN
 *   java tools/textures/Tex.java palette <png...>                 list a texture's colors, most used first
 *
 * Each tools/textures/<name>.txt produces common/src/main/resources/assets/barro/textures/block/<name>.png.
 * pal.txt holds palette letters shared by every spec. Spec lines:
 *
 *   sym=mirror|mirror8|rot|full   how the rows fill the 16x16 tile
 *                                 mirror:  8x8 top-left quadrant (index 7 touches the center), mirrored on both axes
 *                                 mirror8: same, also mirrored on the diagonal; only cells with column >= row are read
 *                                 rot:     8x8 quadrant rotated 90 degrees into each quadrant (pinwheel)
 *                                 full:    16 rows of 16
 *   noise=<seed> <blur>           seeded noise for '.' cells, box-blurred <blur> times with wraparound so it tiles
 *   ~<weight>=<rrggbb>            noise colors from lowest to highest noise, sharing the tile by weight
 *   X=<rrggbb>                    palette letter
 *   // comment
 */
public class Tex {
    static final Path SPECS = Path.of("tools/textures");
    static final Path OUT = Path.of("common/src/main/resources/assets/barro/textures/block");

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            generateAll();
            return;
        }
        switch (args[0]) {
            case "preview" -> preview(args);
            case "palette" -> palette(args);
            default -> throw new IllegalArgumentException("unknown mode " + args[0]);
        }
    }

    static void generateAll() throws Exception {
        List<String> shared = Files.readAllLines(SPECS.resolve("pal.txt"));
        List<Path> specs;
        try (Stream<Path> files = Files.list(SPECS)) {
            specs = files.filter(p -> p.toString().endsWith(".txt") && !p.endsWith("pal.txt")).sorted().toList();
        }
        for (Path spec : specs) {
            String name = spec.getFileName().toString().replace(".txt", "");
            List<String> lines = new ArrayList<>(shared);
            lines.addAll(Files.readAllLines(spec));
            ImageIO.write(render(name, lines), "png", OUT.resolve(name + ".png").toFile());
            System.out.println(name);
        }
    }

    static BufferedImage render(String name, List<String> lines) {
        Map<Character, Integer> palette = new HashMap<>();
        List<int[]> noiseColors = new ArrayList<>(); // {weight, rgb}
        List<String> rows = new ArrayList<>();
        String sym = null;
        long seed = 0;
        int blur = 0;
        for (String line : lines) {
            if (line.isBlank() || line.startsWith("//")) continue;
            if (line.startsWith("sym=")) {
                sym = line.substring(4).trim();
            } else if (line.startsWith("noise=")) {
                String[] parts = line.substring(6).trim().split("\\s+");
                seed = Long.parseLong(parts[0]);
                blur = Integer.parseInt(parts[1]);
            } else if (line.startsWith("~")) {
                String[] parts = line.substring(1).split("=");
                noiseColors.add(new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim(), 16)});
            } else if (line.length() > 2 && line.charAt(1) == '=') {
                palette.put(line.charAt(0), Integer.parseInt(line.substring(2).trim(), 16));
            } else {
                rows.add(line.replace(" ", ""));
            }
        }
        int n = "full".equals(sym) ? 16 : 8;
        if (sym == null || rows.size() != n || rows.stream().anyMatch(r -> r.length() != n))
            throw new IllegalStateException(name + ": expected sym= and " + n + " rows of " + n + " cells, got " + rows);
        int[][] noise = noiseColors.isEmpty() ? null : noise(seed, blur, noiseColors);

        BufferedImage out = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int r;
                int c;
                switch (sym) {
                    case "full" -> { r = y; c = x; }
                    case "rot" -> {
                        if (x < 8 && y < 8) { r = y; c = x; }
                        else if (x >= 8 && y < 8) { r = 15 - x; c = y; }
                        else if (x >= 8) { r = 15 - y; c = 15 - x; }
                        else { r = x; c = 15 - y; }
                    }
                    case "mirror", "mirror8" -> {
                        r = y <= 7 ? y : 15 - y;
                        c = x <= 7 ? x : 15 - x;
                        if (sym.equals("mirror8") && r > c) { int t = r; r = c; c = t; }
                    }
                    default -> throw new IllegalStateException(name + ": unknown sym " + sym);
                }
                char cell = rows.get(r).charAt(c);
                Integer color = cell == '.' ? (noise == null ? null : noise[y][x]) : palette.get(cell);
                if (color == null) throw new IllegalStateException(name + ": no color for '" + cell + "'");
                out.setRGB(x, y, color);
            }
        }
        return out;
    }

    // Noise is sampled at the real pixel position, never mirrored, so symmetric motifs still get an irregular glaze.
    static int[][] noise(long seed, int blur, List<int[]> colors) {
        Random random = new Random(seed);
        double[][] value = new double[16][16];
        for (double[] row : value)
            for (int x = 0; x < 16; x++) row[x] = random.nextDouble();
        for (int pass = 0; pass < blur; pass++) {
            double[][] blurred = new double[16][16];
            for (int y = 0; y < 16; y++)
                for (int x = 0; x < 16; x++)
                    for (int dy = -1; dy <= 1; dy++)
                        for (int dx = -1; dx <= 1; dx++)
                            blurred[y][x] += value[(y + dy + 16) % 16][(x + dx + 16) % 16] / 9;
            value = blurred;
        }
        // Rank the pixels by noise and hand out colors by weight, so proportions are exact whatever the seed.
        double[][] v = value;
        Integer[] order = new Integer[256];
        for (int i = 0; i < 256; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingDouble(i -> v[i / 16][i % 16]));
        int total = colors.stream().mapToInt(c -> c[0]).sum();
        int[][] out = new int[16][16];
        for (int rank = 0; rank < 256; rank++) {
            double share = (rank + 0.5) * total / 256;
            int cumulative = 0;
            for (int[] color : colors) {
                cumulative += color[0];
                if (share < cumulative) {
                    out[order[rank] / 16][order[rank] % 16] = color[1];
                    break;
                }
            }
        }
        return out;
    }

    static void preview(String[] args) throws Exception {
        int scale = Integer.parseInt(args[2]);
        int tiles = Integer.parseInt(args[3]);
        int count = args.length - 4;
        int gap = 8;
        int cell = 16 * scale * tiles;
        BufferedImage out = new BufferedImage(count * (cell + gap), cell, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < count; i++) {
            BufferedImage img = ImageIO.read(new File(args[4 + i]));
            for (int y = 0; y < cell; y++)
                for (int x = 0; x < cell; x++)
                    out.setRGB(i * (cell + gap) + x, y, img.getRGB((x / scale) % 16, (y / scale) % 16));
        }
        ImageIO.write(out, "png", new File(args[1]));
    }

    static void palette(String[] args) throws Exception {
        for (int i = 1; i < args.length; i++) {
            BufferedImage img = ImageIO.read(new File(args[i]));
            Map<Integer, Integer> counts = new HashMap<>();
            for (int y = 0; y < img.getHeight(); y++)
                for (int x = 0; x < img.getWidth(); x++)
                    counts.merge(img.getRGB(x, y) & 0xFFFFFF, 1, Integer::sum);
            StringBuilder line = new StringBuilder(new File(args[i]).getName() + ":");
            counts.entrySet().stream().sorted((p, q) -> q.getValue() - p.getValue())
                    .forEach(e -> line.append(String.format(" #%06x(%d)", e.getKey(), e.getValue())));
            System.out.println(line);
        }
    }
}
