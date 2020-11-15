import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Helper {

    private static int height;
    private static int width;
    private static int[][] grayscaleArray;
    private static File f;
    private static BufferedImage OriginImg;
    private static BufferedImage processedImg;
    private static int[][] labeledGrayscaleArray;
    private static ArrayList<double[]> cell0List = new ArrayList();
    private static ArrayList<int[]> cell1List = new ArrayList();
    private static ArrayList<int[]> cell2List = new ArrayList();
    private static int[] cell0ParentTable;
    private static int[] cell1ParentTable;
    private static String fileName = "output.jpg";
    private static int maxHeight = 0;
    private static int maxWidth = 0;
    private static int minHeight = 99999;
    private static int minWidth = 99999;
    private static int[][] rangePoints;

    public static void main(String[] args) {

//        easy24708_1_1();
//        easy24708_1_2();
        easy24708_1_3();
    }

    private static void easy24708_1_3() {
        getGreyImage("src/input/24708_1_3.jpg", 160);
        int largestIndex = labelComponents();
        getLargestComponent(largestIndex);
        drawImg();
//        buildCC();
//        thin(new double[]{70.0, 1});
    }

    private static void easy24708_1_2() {
        getGreyImage("src/input/24708_1_2.jpg", 164);
        int largestIndex = labelComponents();
        getLargestComponent(largestIndex);

        dilate();
        dilate();
        dilate();
        dilate();
        erode();
        buildCC();
        thin(new double[]{70.0, 1});
    }

    private static void easy24708_1_1() {
        getGreyImage("src/input/24708.jpg", 189);
        int largestIndex = labelComponents();
        getLargestComponent(largestIndex);
        dilate();
        dilate();
        dilate();
        erode();
        buildCC();
        thin(new double[]{19.0, 0.2});
    }

    private static void getGreyImageFromInput(BufferedImage img) {
        OriginImg = GUI.deepCopy(img);
        processedImg = GUI.deepCopy(img);

        width = OriginImg.getWidth();
        height = OriginImg.getHeight();
        grayscaleArray = new int[height][width];


    }

    private static void thresholdImg(int thrValue) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y >= minHeight && y <= maxHeight && x >= minWidth && x <= maxWidth) {
                    grayscaleArray[y][x] = new Color(OriginImg.getRGB(x, y)).getRed();
                    int newPixelValue = threshold(thrValue, grayscaleArray[y][x]);
                    grayscaleArray[y][x] = newPixelValue;
                } else {
                    grayscaleArray[y][x] = (255 << 24) | (0);
                }

            }
        }
    }

    public ArrayList<double[]> processImg(BufferedImage img, int[][] rectangle, int thresholdValue) {
        maxHeight = 0;
        maxWidth = 0;
        minHeight = 99999;
        minWidth = 99999;

        for (int i = 0; i < rectangle.length; i++) {
            maxHeight = Math.max(maxHeight, rectangle[i][1]);
            maxWidth = Math.max(maxWidth, rectangle[i][0]);
            minHeight = Math.min(minHeight, rectangle[i][1]);
            minWidth = Math.min(minWidth, rectangle[i][0]);
            System.out.println(rectangle[i][0]+" "+rectangle[i][1]);
        }

        System.out.println(maxHeight+" "+minHeight);
        System.out.println(maxWidth+" "+minWidth);
        getGreyImageFromInput(img);
        thresholdImg(thresholdValue);
        int largestIndex = labelComponents();
        getLargestComponent(largestIndex);
        dilate();
        dilate();
        dilate();
        erode();
        buildCC();
        return thin(new double[]{19.0, 0.2});
    }

    private static int calThreshold() {
        int threshold = 0;
        ArrayList<Integer> thresholds = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y >= minHeight && y <= maxHeight && x >= minWidth && x <= maxWidth) {
                    thresholds.add(new Color(OriginImg.getRGB(x, y)).getRed());
                }
            }
        }
        Collections.sort(thresholds);
        return thresholds.get(thresholds.size() / 2);
    }

    private static void open(int iteration) {
        for (int i = 0; i < iteration; i++) {
            erode();
            dilate();
        }
    }

    private static void close(int iteration) {
        for (int i = 0; i < iteration; i++) {
            dilate();
            erode();
        }
    }

    private static void dilate() {
        int[][] structure = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        int iteration = 1;
        for (int i = 0; i < iteration; i++) {
            int[][] temp = new int[height][width];
            for (int m = 0; m < height; m++) {
                for (int n = 0; n < width; n++) {
                    temp[m][n] = grayscaleArray[m][n];
                }
            }
            for (int m = 0; m < height; m++) {
                for (int n = 0; n < width; n++) {
                    if (temp[m][n] == 1) {
                        for (int j = 0; j < structure.length; j++) {
                            if (m + structure[j][0] >= 0 && n + structure[j][1] >= 0 && m + structure[j][0] < height && n + structure[j][1] < width) {
                                grayscaleArray[m + structure[j][0]][n + structure[j][1]] = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void erode() {
        int[][] structure = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        int iteration = 1;
        for (int i = 0; i < iteration; i++) {
            int[][] temp = new int[height][width];
            for (int m = 0; m < height; m++) {
                for (int n = 0; n < width; n++) {
                    temp[m][n] = grayscaleArray[m][n];
                }
            }
            for (int m = 0; m < height; m++) {
                for (int n = 0; n < width; n++) {
                    if (temp[m][n] == 0) {
                        for (int j = 0; j < structure.length; j++) {
                            if (m + structure[j][0] >= 0 && n + structure[j][1] >= 0 && m + structure[j][0] < height && n + structure[j][1] < width) {
                                grayscaleArray[m + structure[j][0]][n + structure[j][1]] = 0;
                            }
                        }
                    }
                }
            }
        }
    }


    private static ArrayList<double[]> thin(double[] thresholds) {
        countSimplePairs();
        boolean[] cell0tag = new boolean[cell0List.size()];
        boolean[] cell1tag = new boolean[cell1List.size()];
        boolean[] cell2tag = new boolean[cell2List.size()];
        int[] cell0Set = new int[cell0List.size()];
        int[] cell1Set = new int[cell1List.size()];
        int iterationNum = 1;
        ArrayList<int[][]> deletedCells;


        Arrays.fill(cell0tag, true);
        Arrays.fill(cell1tag, true);
        Arrays.fill(cell2tag, true);
        for (int i = 0; i < cell0Set.length; i++) {
            if (cell0ParentTable[i] == 0) cell0Set[i] = 0;
            else cell0Set[i] = -1;
        }
        for (int i = 0; i < cell1Set.length; i++) {
            if (cell1ParentTable[i] == 0) cell1Set[i] = 0;
            else cell1Set[i] = -1;
        }


        while (true) {
            deletedCells = new ArrayList<>();
            for (int i = 0; i < cell1List.size(); i++) {
                if (cell1tag[i]) {
                    for (int j = 0; j < cell1List.get(i).length; j++) {
                        if (cell0ParentTable[cell1List.get(i)[j] - 1] == 1) {
                            deletedCells.add(new int[][]{{1, i}, {0, cell1List.get(i)[j] - 1}});
                            break;
                        }
                    }
                }
            }
            for (int i = 0; i < cell2List.size(); i++) {
                if (cell2tag[i]) {
                    for (int j = 0; j < cell2List.get(i).length; j++) {
                        if (cell1ParentTable[cell2List.get(i)[j] - 1] == 1) {
                            deletedCells.add(new int[][]{{2, i}, {1, cell2List.get(i)[j] - 1}});
                            break;
                        }
                    }
                }
            }

            for (int i = 0; i < deletedCells.size(); i++) {
                for (int j = 0; j < 2; j++) {
                    int k = deletedCells.get(i)[j][0];
                    int m = deletedCells.get(i)[j][1];
                    if (k == 1 && cell1Set[m] >= 0 && (iterationNum - cell1Set[m]) > thresholds[0] &&
                            (1 - (double) (cell1Set[m] / iterationNum)) > thresholds[1]) {
                        deletedCells.remove(i);
                        i--;
                        break;
                    }
                }
            }

            if (deletedCells.size() == 0) break;
            for (int i = 0; i < deletedCells.size(); i++) {
                for (int j = 0; j < 2; j++) {
                    int k = deletedCells.get(i)[j][0];
                    int m = deletedCells.get(i)[j][1];
                    if (k == 0) cell0tag[m] = false;
                    else if (k == 1) cell1tag[m] = false;
                    else cell2tag[m] = false;
                    if (k == 1) {
                        for (int n = 0; n < cell1List.get(m).length; n++) {
                            cell0ParentTable[cell1List.get(m)[n] - 1] -= 1;
                        }
                    } else if (k == 2) {

                        for (int n = 0; n < cell2List.get(m).length; n++) {
                            cell1ParentTable[cell2List.get(m)[n] - 1] -= 1;
                            if (cell1ParentTable[cell2List.get(m)[n] - 1] == 0)
                                cell1Set[cell2List.get(m)[n] - 1] = iterationNum;

                        }
                    }
                }
            }
            iterationNum += 1;
        }

        int deletedNum = 0;
        ArrayList<double[]> thinedCell0List = new ArrayList<>();
        ArrayList<int[]> thinedCell1List = new ArrayList<>();
        ArrayList<int[]> thinedCell2List = new ArrayList<>();
        int[] numberDeletedBefore = new int[cell0tag.length + 1];

        for (int i = 0; i < cell0tag.length; i++) {
            if (cell0tag[i]) {
                deletedNum++;
                thinedCell0List.add(cell0List.get(i));
            }
            numberDeletedBefore[i + 1] = deletedNum;
        }
        for (int i = 0; i < cell1List.size(); i++) {
            for (int j = 0; j < cell1List.get(i).length; j++) {
                cell1List.get(i)[j] -= numberDeletedBefore[cell1List.get(i)[j]];
            }
        }

        deletedNum = 0;
        numberDeletedBefore = new int[cell1tag.length + 1];
        for (int i = 0; i < cell1tag.length; i++) {
            if (!cell1tag[i]) {
                deletedNum++;
                thinedCell1List.add(cell1List.get(i));
            }
            numberDeletedBefore[i + 1] = deletedNum;
        }
        for (int i = 0; i < cell2List.size(); i++) {
            for (int j = 0; j < cell2List.get(i).length; j++) {
                cell2List.get(i)[j] -= numberDeletedBefore[cell2List.get(i)[j]];
            }
        }

        deletedNum = 0;
        for (int i = 0; i < cell2tag.length; i++) {
            if (!cell2tag[i]) {
                deletedNum++;
                thinedCell2List.add(cell2List.get(i));
            }
        }
        Graphics2D g = processedImg.createGraphics();
        g.setColor(Color.BLUE);
        for (int i = 0; i < thinedCell0List.size(); i++) {
            double y1 = thinedCell0List.get(i)[0];
            double x1 = thinedCell0List.get(i)[1];
            g.drawLine((int) x1, (int) y1, (int) x1, (int) y1);
        }
        writeFile();
        System.out.println("Length:" + (double) thinedCell0List.size() / 3.06);
        return thinedCell0List;
    }

    private static void countSimplePairs() {
        cell0ParentTable = new int[cell0List.size()];
        cell1ParentTable = new int[cell1List.size()];

        for (int i = 0; i < cell1List.size(); i++) {
            for (int j = 0; j < cell1List.get(i).length; j++) {
                cell0ParentTable[cell1List.get(i)[j] - 1] += 1;
            }
        }

        for (int i = 0; i < cell2List.size(); i++) {
            for (int j = 0; j < cell2List.get(i).length; j++) {
                cell1ParentTable[cell2List.get(i)[j] - 1] += 1;
            }
        }
    }

    private static void buildCC() {
        int index0cell = 1;
        int index1cell = 1;
        int index2cell = 1;
        cell0List = new ArrayList<>();
        cell1List = new ArrayList<>();
        cell2List = new ArrayList<>();
        int[][] indexedArray = new int[2 * height + 1][2 * width + 1];
        int[][] fourCorners = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        int[][] fourBoundaries = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grayscaleArray[y][x] != 0) {
                    for (int m = 0; m < 4; m++) {
                        int newx = 2 * x + fourCorners[m][0];
                        int newy = 2 * y + fourCorners[m][1];
                        if (indexedArray[newy][newx] == 0) {
                            indexedArray[newy][newx] = index0cell;
                            cell0List.add(new double[]{(double) newy / 2, (double) newx / 2});   // you  may need float or double here
                            index0cell++;
                        }
                    }

                    for (int m = 0; m < 4; m++) {
                        int newx = 2 * x + fourBoundaries[m][0];
                        int newy = 2 * y + fourBoundaries[m][1];
                        if (indexedArray[newy][newx] == 0) {
                            indexedArray[newy][newx] = index1cell;
                            if (newy % 2 == 0) {
                                cell1List.add(new int[]{indexedArray[newy - 1][newx], indexedArray[newy + 1][newx]});
                            } else {
                                cell1List.add(new int[]{indexedArray[newy][newx - 1], indexedArray[newy][newx + 1]});
                            }
                            index1cell++;
                        }
                    }
                    indexedArray[2 * y][2 * x] = index2cell;
                    index2cell++;
                    cell2List.add(new int[]{indexedArray[2 * y - 1][2 * x], indexedArray[2 * y + 1][2 * x],
                            indexedArray[2 * y][2 * x - 1], indexedArray[2 * y][2 * x + 1]});
                }
            }
        }
    }

    private static void drawCC2D() {
        Graphics2D g = processedImg.createGraphics();
        g.setColor(Color.GREEN);
//        g.fillRect(0,0,12,12);
//        BasicStroke bs = new BasicStroke(5);
//        g.setStroke(bs);
//        for (int i = 0; i < cell1List.size(); i++) {
//
//            double y1 = (cell0List.get(cell1List.get(i)[0] - 1)[0]);
//            double x1 = (cell0List.get(cell1List.get(i)[0] - 1)[1]);
//            double y2 = (cell0List.get(cell1List.get(i)[1] - 1)[0]);
//            double x2 = (cell0List.get(cell1List.get(i)[1] - 1)[1]);
//            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
//        }

        for (int i = 0; i < cell0List.size(); i++) {
            double y1 = cell0List.get(i)[0];
            double x1 = cell0List.get(i)[1];
            g.drawLine((int) x1, (int) y1, (int) x1, (int) y1);
        }
        writeFile();
    }

    private static void drawImg() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grayscaleArray[y][x] == 1) {
                    processedImg.setRGB(x, y, (255 << 24) | (255 << 16) | (255 << 8) | 255);
                } else {
                    processedImg.setRGB(x, y, (255 << 24) | (0));
                }
            }
        }
        writeFile();
    }

    private static void getLargestComponent(int largestIndex) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (labeledGrayscaleArray[y][x] == largestIndex) {
                    grayscaleArray[y][x] = 1;
                } else {
                    grayscaleArray[y][x] = 0;
                }
            }
        }
    }

    private static int labelComponents() {
        int index = 1;
        int largestComponentIndex = 0;
        int maxElements = 0;
        ArrayList<Integer> indexElementsMap = new ArrayList<>();
        labeledGrayscaleArray = new int[height][width];
        int[][] fourConn = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
//        int[][] fourConn = { {-1, 0},  {0, 1}, {1, 0},  {0, -1}};
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grayscaleArray[y][x] == -1 && labeledGrayscaleArray[y][x] == 0) {
                    Queue<int[]> queue = new LinkedList<>();
                    int elements = 1;
                    queue.add(new int[]{y, x});
                    labeledGrayscaleArray[y][x] = index;
                    while (queue.size() > 0) {
                        int[] popedPos = queue.poll();
                        for (int i = 0; i < 8; i++) {
                            int newy = popedPos[0] + fourConn[i][0];
                            int newx = popedPos[1] + fourConn[i][1];
                            if (newx >= 0 && newx < width && newy >= 0 && newy < height &&
                                    grayscaleArray[newy][newx] == -1 && labeledGrayscaleArray[newy][newx] != index) {
                                labeledGrayscaleArray[newy][newx] = index;
                                queue.add(new int[]{newy, newx});
                                elements++;
                            }
                        }
                    }
                    if (elements > maxElements) {
                        largestComponentIndex = index;
                        maxElements = elements;
                    }
                    if (elements > 10000) {
                        indexElementsMap.add(index);
                    }
                    index += 1;
                }
            }
        }
//        return indexElementsMap.get(1);
        return largestComponentIndex;
    }

    private static int threshold(int thrValue, int grayscaleValue) {
        if (grayscaleValue > thrValue) {
            return (255 << 24) | (255 << 16) | (255 << 8) | 255;
        } else {
            return (255 << 24) | (0);
        }
    }

    private static void getGreyImage(String filename, int thrValue) {
        OriginImg = null;
        int slashIndex = filename.lastIndexOf('/');
        fileName = filename.substring(slashIndex);
        try {
            File image = new File(filename);
            OriginImg = ImageIO.read(image);
            processedImg = OriginImg;
        } catch (IOException e) {
            System.out.println(e);
        }

        width = OriginImg.getWidth();
        height = OriginImg.getHeight();
        grayscaleArray = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grayscaleArray[y][x] = new Color(OriginImg.getRGB(x, y)).getRed();
                int newPixelValue = threshold(thrValue, grayscaleArray[y][x]);
                grayscaleArray[y][x] = newPixelValue;
            }
        }
    }

    private static void writeFile() {
        String output = "src/output/output.jpg";
        try {
            f = new File(output);
            ImageIO.write(processedImg, "jpg", f);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
