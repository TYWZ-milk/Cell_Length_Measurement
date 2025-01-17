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
//        test();
//        easy24708_1_1();
        easy24708_1_2();
//        easy24708_1_3();
//        medium24708_1_6();
    }

    private static void test(){
        rangePoints = new int[][]{{10, 10}, {2030, 2030}};
        getGreyImage("src/input/24708_1_3.jpg", 155);
        drawImg();
    }

    private static void medium24708_1_6() {
        rangePoints = new int[][]{{1792, 520}, {580, 1672}};
        getGreyImage("src/input/24708.1_6 at 20X.jpg", 180);
        ArrayList<Integer> largestIndex = labelComponents(2);
        getLargestComponents(largestIndex);
        dilate();
        dilate();
        dilate();
        erode();
        drawImg();
        buildCC();
        thin(new double[]{10.0, 0.1});
    }

    private static void easy24708_1_3() {
        rangePoints = new int[][]{{1792, 520}, {580, 1672}};
        getGreyImage("src/input/24708_1_3.jpg", 180);
        ArrayList<Integer> largestIndex = labelComponents(2);
        getLargestComponents(largestIndex);
        dilate();
        dilate();
        dilate();
        erode();
        buildCC();
        thin(new double[]{70.0, 1});
    }

    private static void easy24708_1_2() {
        rangePoints = new int[][]{{1792, 520}, {580, 1672}};
        getGreyImage("src/input/24708_1_2.jpg", 164);
        ArrayList<Integer> largestIndex = labelComponents(2);
        getLargestComponents(largestIndex);
        dilate();
        dilate();
        dilate();
        dilate();
        erode();
        buildCC();
        thin(new double[]{70.0, 1});
    }

    private static void easy24708_1_1() {
//430 132
//128 286
        rangePoints = new int[][]{{1720, 528}, {512, 1144}};
        getGreyImage("src/input/24708.jpg", 165);

        ArrayList<Integer> largestIndex = labelComponents(2);
        getLargestComponents(largestIndex);
        dilate();
        dilate();
        dilate();
        erode();
        buildCC();
        thin(new double[]{110.0, 1.0});

    }

    private static void getGreyImageFromInput(BufferedImage img) {
        OriginImg = GUI.deepCopy(img);
        processedImg = GUI.deepCopy(img);

        width = OriginImg.getWidth();
        height = OriginImg.getHeight();
        grayscaleArray = new int[height][width];


    }

    private static boolean pointInRange(int x, int y) {
        int crossTimes = 0;
        for (int i = 0; i < rangePoints.length; i++) {
            int[] point1 = new int[2];
            int[] point2 = new int[2];
            point1[0] = rangePoints[i][0];
            point1[1] = rangePoints[i][1];
            if (i == rangePoints.length - 1) {
                point2[0] = rangePoints[0][0];
                point2[1] = rangePoints[0][1];
            } else {
                point2[0] = rangePoints[i + 1][0];
                point2[1] = rangePoints[i + 1][1];
            }

            double slope = ((double) (point2[1] - point1[1])) / ((double) (point2[0] - point1[0]));
            boolean condition1 = (point1[0] <= x) && (x < point2[0]);
            boolean condition2 = (point2[0] <= x) && (x < point1[0]);
            boolean above = (y < slope * (x - point1[0]) + point1[1]);
            if ((condition1 || condition2) && above) crossTimes++;

        }
        return crossTimes % 2 != 0;
    }

    private static void thresholdImg() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grayscaleArray[y][x] = new Color(OriginImg.getRGB(x, y)).getRed();
            }
        }

        adaptiveThreshold_Mean();

//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (y >= minHeight && y <= maxHeight && x >= minWidth && x <= maxWidth) {
//                    if ((rangePoints.length == 2) || (rangePoints.length > 2 && pointInRange(x, y))) {
//                        grayscaleArray[y][x] = new Color(OriginImg.getRGB(x, y)).getRed();
//                        int newPixelValue = threshold(thrValue, grayscaleArray[y][x]);
//                        grayscaleArray[y][x] = newPixelValue;
//                    } else {
//                        grayscaleArray[y][x] = (255 << 24) | (0);
//                    }
//                } else {
//                    grayscaleArray[y][x] = (255 << 24) | (0);
//                }
//
//            }
//        }
    }

    public ArrayList<double[]> processImg(BufferedImage img, int[][] rectangle, int k) {
        maxHeight = 0;
        maxWidth = 0;
        minHeight = 99999;
        minWidth = 99999;
        rangePoints = new int[rectangle.length][2];
        for (int i = 0; i < rectangle.length; i++) {
            maxHeight = Math.max(maxHeight, rectangle[i][1]);
            maxWidth = Math.max(maxWidth, rectangle[i][0]);
            minHeight = Math.min(minHeight, rectangle[i][1]);
            minWidth = Math.min(minWidth, rectangle[i][0]);
            rangePoints[i][0] = rectangle[i][0];
            rangePoints[i][1] = rectangle[i][1];
        }

        getGreyImageFromInput(img);
        thresholdImg();
        ArrayList<Integer> largestIndex = labelComponents(k);
        getLargestComponents(largestIndex);
        dilate();
        dilate();
        dilate();
        erode();
        buildCC();
        return thin(new double[]{10.0, 0.1});
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
        thinedCell0List = processFinalResult(thinedCell0List);
        Graphics2D g = processedImg.createGraphics();
        g.setColor(Color.BLUE);
        for (int i = 0; i < thinedCell0List.size(); i++) {
            double y1 = thinedCell0List.get(i)[0];
            double x1 = thinedCell0List.get(i)[1];
            g.drawLine((int) x1, (int) y1, (int) x1, (int) y1);
        }
        writeFile();
        System.out.println("The Sperm Length: " + (double) thinedCell0List.size() *Math.sqrt(2)/ 3.06 + " micrometers");
        return thinedCell0List;
    }

    private static ArrayList<double[]> processFinalResult(ArrayList<double[]> thinedList) {
        ArrayList<double[]> finalRes = new ArrayList<>();
        int[][] eightConn = {{-1, -1}, {1, 1}, {-1, 0}, {1, 0}, {-1, 1}, {1, -1}, {0, 1}, {0, -1}};
        boolean[][] matrix = new boolean[width][height];
        for (int i = 0; i < thinedList.size(); i++) {
            int x = (int) thinedList.get(i)[0];
            int y = (int) thinedList.get(i)[1];
            matrix[x][y] = true;
        }
        for (int i = 0; i < width; i++) {
            for( int k = 0;k<height;k++) {
                if(matrix[i][k]) {
                    ArrayList<int[]> neighbors = new ArrayList<>();
                    for (int j = 0; j < 8; j++) {
                        int newx = eightConn[j][0] + i;
                        int newy = eightConn[j][1] + k;
                        if (matrix[newx][newy]) {
                            neighbors.add(new int[]{newx, newy});
                        }
                    }
                    if (neighbors.size() > 2) {
                        for (int j = 0; j < neighbors.size() - 2; j++) {
                            matrix[neighbors.get(j)[0]][neighbors.get(j)[1]] = false;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for( int k = 0;k<height;k++) {
                if(matrix[i][k]){
                    finalRes.add(new double[]{i,k});
                }
            }
        }

        return finalRes;
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

    private static void getLargestComponents(ArrayList<Integer> largestIndex) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (largestIndex.contains(labeledGrayscaleArray[y][x])) {
                    grayscaleArray[y][x] = 1;
                } else {
                    grayscaleArray[y][x] = 0;
                }
            }
        }
    }

    private static ArrayList<Integer> labelComponents(int k) {
        int index = 1;
        int largestComponentIndex = 0;
        int maxElements = 0;
//        ArrayList<Integer> indexElementsMap = new ArrayList<>();
        TreeMap<Integer,Integer> indexElementsMap = new TreeMap<>();
        labeledGrayscaleArray = new int[height][width];
//        int[][] fourConn = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
        int[][] fourConn = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grayscaleArray[y][x] == -1 && labeledGrayscaleArray[y][x] == 0) {
                    Queue<int[]> queue = new LinkedList<>();
                    int elements = 1;
                    queue.add(new int[]{y, x});
                    labeledGrayscaleArray[y][x] = index;
                    while (queue.size() > 0) {
                        int[] popedPos = queue.poll();
                        for (int i = 0; i < 4; i++) {
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
                    if (elements > 400) {
//                        indexElementsMap.add(index);
                        indexElementsMap.put(elements,index);
                    }
                    index += 1;
                }
            }
        }
        ArrayList<Integer>keys = new ArrayList<>(indexElementsMap.keySet());
        ArrayList<Integer>kLargestComponents = new ArrayList<>();
        for(int i =keys.size()-1;i>keys.size()-k-1 && i>=0;i--){
            kLargestComponents.add(indexElementsMap.get(keys.get(i)));
        }
//        return indexElementsMap.get(1);
//        return largestComponentIndex;
        return kLargestComponents;
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
        maxHeight = 0;
        maxWidth = 0;
        minHeight = 99999;
        minWidth = 99999;
        for (int i = 0; i < rangePoints.length; i++) {
            maxHeight = Math.max(maxHeight, rangePoints[i][1]);
            maxWidth = Math.max(maxWidth, rangePoints[i][0]);
            minHeight = Math.min(minHeight, rangePoints[i][1]);
            minWidth = Math.min(minWidth, rangePoints[i][0]);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grayscaleArray[y][x] = new Color(OriginImg.getRGB(x, y)).getRed();
            }
        }

        adaptiveThreshold_Mean();

//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (y >= minHeight && y <= maxHeight && x >= minWidth && x <= maxWidth) {
//                    if ((rangePoints.length == 2) || (rangePoints.length > 2 && pointInRange(x, y))) {
//                        grayscaleArray[y][x] = new Color(OriginImg.getRGB(x, y)).getRed();
//                        int newPixelValue = threshold(thrValue, grayscaleArray[y][x]);
//                        grayscaleArray[y][x] = newPixelValue;
//                    } else {
//                        grayscaleArray[y][x] = (255 << 24) | (0);
//                    }
//                } else {
//                    grayscaleArray[y][x] = (255 << 24) | (0);
//                }
//            }
//        }
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


    //I got my idea from: https://docs.opencv.org/master/d7/d4d/tutorial_py_thresholding.html
    //I didn't copy and paste code. I have my understanding after reading those code. I know what each line of my code does.
    private static void adaptiveThreshold_Mean() {
        int[][] newImg = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y >= minHeight && y <= maxHeight && x >= minWidth && x <= maxWidth &&
                        ((rangePoints.length == 2) || (rangePoints.length > 2 && pointInRange(x, y)))) {
                    int sum = 0, count = 0;
                    for (int i = -5; i <= 5; i++) {
                        if (x + i < 0) continue;
                        if (x + i >= width) break;
                        for (int j = -5; j <= 5; j++) {
                            if (y + j < 0) continue;
                            if (y + j >= height) break;
                            sum += grayscaleArray[y + i][x + j];
                            count++;
                        }
                    }
                    if (grayscaleArray[y][x] > sum / count)
                        newImg[y][x] = (255 << 24) | (255 << 16) | (255 << 8) | 255;
                    else
                        newImg[y][x] = (255 << 24) | (0);

                } else {
                    newImg[y][x] = (255 << 24) | (0);
                }
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grayscaleArray[y][x] = newImg[y][x];
            }
        }
    }
}