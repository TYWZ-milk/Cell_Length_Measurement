import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GUI extends JFrame {

    private int[][] quadrupleRangePoints;
    private BufferedImage originImg;
    private BufferedImage drewImg;
    private JLabel imageLabel = new JLabel();
    private Graphics2D RectImg;
    private Image scaledImg;
    private ArrayList<int[]> rangePoints;
    private boolean drawPolygon;
    private boolean drawRect;

    public static void main(String[] args) {
        GUI app = new GUI();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void valueInit() {
        drawPolygon = false;
        rangePoints = new ArrayList<>();
    }

    private GUI() {
        setLayout(null);
        valueInit();

        final JFileChooser fc = new JFileChooser("src/input");

        //text length
        JLabel textLength = new JLabel("The Sperm Length: ");
        textLength.setVisible(true);
        textLength.setBounds(400, 550, 300, 40);

        //Image name
        JLabel imgName = new JLabel("Image: ");
        imgName.setVisible(true);
        imgName.setBounds(500, 20, 300, 40);

        //erase button
        JButton eraseBtn = new JButton("Erase");
        eraseBtn.setBounds(50, 540, 140, 40);
        eraseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickErase();
                textLength.setText("Length: ");
            }
        });

        //download button
        JButton downloadBtn = new JButton("Download");
        downloadBtn.setBounds(50, 580, 140, 40);
        downloadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filename = "result" +
                        "_" + imgName.getText().split("Image: ")[1];
                filename = filename.replaceFirst("jpg","png").replace(' ','_');
                Image downloadImg = drewImg.getScaledInstance(2048, 2048, java.awt.Image.SCALE_SMOOTH);
                try {
                    ImageIO.write(toBufferedImage(downloadImg) , "png", new File(filename));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //undo button
        JButton undoBtn = new JButton("Undo");
        undoBtn.setBounds(50, 290, 140, 40);
        undoBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        //text step1
        JLabel textUpload = new JLabel("Step1: Upload your image");
        textUpload.setVisible(true);
        textUpload.setBounds(50, 70, 300, 40);

        //upload button
        JButton uploadBtn = new JButton("Upload");
        uploadBtn.setBounds(50, 100, 140, 40);
        uploadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickUpload(fc, imgName);
            }
        });

        //increase contrast button
        JButton increaseContrastBtn = new JButton("Increase Contrast");
        increaseContrastBtn.setBounds(50, 140, 140, 40);
        increaseContrastBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                increaseContrast();
            }
        });

        //text step2
        JLabel textDrawRange = new JLabel("Step2: Draw region-of-interest");
        textDrawRange.setVisible(true);
        textDrawRange.setBounds(50, 180, 300, 40);

        //draw rectangle button
        JButton drawRectBtn = new JButton("Draw a rectangle");
        drawRectBtn.setBounds(50, 210, 140, 40);
        drawRectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                drawRect = true;
                rangePoints = new ArrayList<>();
            }
        });

        //draw polygon button
        JButton drawPolyBtn = new JButton("Draw a polygon");
        drawPolyBtn.setBounds(50, 250, 140, 40);
        drawPolyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                drawPolygon = true;
                rangePoints = new ArrayList<>();
            }
        });

        //complete drawing button
        JButton completedrawBtn = new JButton("Complete");
        completedrawBtn.setBounds(50, 330, 140, 40);
        completedrawBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                completeDrawing();
            }
        });

        //step3 Get k largest components.
        JLabel textStep3 = new JLabel("Step3: Get k largest components.");
        textStep3.setBounds(50, 370, 260, 40);
        //input k value
        JLabel textKvalue = new JLabel("Input k value(1~10): ");
        textKvalue.setBounds(50, 390, 260, 40);

        JTextField kValue = new JTextField(12);
        kValue.setBounds(50, 420, 140, 40);

        //imageLabel
        imageLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                imgLabelListner(me);
            }
        });

        //text step4
        JLabel textRun = new JLabel("Step4: Run");
        textRun.setVisible(true);
        textRun.setBounds(50, 450, 300, 40);

        //run button
        JButton runBtn = new JButton("Run");
        runBtn.setBounds(50, 490, 140, 40);
        runBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                quadrupleRangePoints = new int[rangePoints.size()][2];
                for (int i = 0; i < quadrupleRangePoints.length; i++) {
                    quadrupleRangePoints[i] = new int[]{rangePoints.get(i)[0] * 4, rangePoints.get(i)[1] * 4};
                }
                Helper helper = new Helper();
                ArrayList<double[]> result = helper.processImg(originImg, quadrupleRangePoints, Integer.parseInt(kValue.getText()));
                textLength.setText("The Sperm Length: " + (int) (result.size() * Math.sqrt(2) / (3.06)) + " micrometers");
                RectImg = drewImg.createGraphics();
                RectImg.setColor(Color.BLUE);
                for (int i = 0; i < result.size(); i++) {
                    double y1 = result.get(i)[0] / 4;
                    double x1 = result.get(i)[1] / 4;
                    RectImg.drawLine((int) x1, (int) y1, (int) x1, (int) y1);
                }
                RectImg.dispose();
                imageLabel.setIcon(new ImageIcon(drewImg));
            }
        });


        //draw GUI
        add(downloadBtn);
        add(kValue);
        add(textKvalue);
        add(textStep3);
        add(increaseContrastBtn);
        add(textRun);
        add(completedrawBtn);
        add(textDrawRange);
        add(textUpload);
        add(drawPolyBtn);
        add(undoBtn);
        add(imgName);
        add(runBtn);
        add(drawRectBtn);
        add(uploadBtn);
        add(eraseBtn);
        add(textLength);
        setSize(1000, 800);
        setVisible(true);
    }

    private void increaseContrast() {
        RescaleOp op = new RescaleOp(1.2f, 0, null);
        originImg = op.filter(originImg, originImg);
        scaledImg = originImg.getScaledInstance(512, 512, java.awt.Image.SCALE_SMOOTH);
        drewImg = deepCopy(toBufferedImage(scaledImg));
        imageLabel.setIcon(null);
        imageLabel.setIcon(new ImageIcon(scaledImg));
        imageLabel.setBounds(300, 50, 512, 512);
        add(imageLabel);
        imageLabel.setVisible(true);
        pack();
        setSize(1000, 800);
    }


    private void undo() {
        if (rangePoints.size() > 0) {
            rangePoints.remove(rangePoints.size() - 1);
            onClickErase();
            RectImg = drewImg.createGraphics();
            RectImg.setColor(Color.RED);
            for (int i = 0; i < rangePoints.size(); i++) {
                RectImg.drawOval(rangePoints.get(i)[0], rangePoints.get(i)[1], 3, 3);
            }
            RectImg.dispose();
            imageLabel.setIcon(new ImageIcon(drewImg));
        }
    }

    private void completeDrawing() {
        if (rangePoints.size() > 0) {
            RectImg = drewImg.createGraphics();
            RectImg.setColor(Color.RED);
            if (drawRect) {
                RectImg.drawLine(rangePoints.get(0)[0], rangePoints.get(1)[1], rangePoints.get(0)[0], rangePoints.get(0)[1]);
                RectImg.drawLine(rangePoints.get(1)[0], rangePoints.get(0)[1], rangePoints.get(0)[0], rangePoints.get(0)[1]);
                RectImg.drawLine(rangePoints.get(1)[0], rangePoints.get(1)[1], rangePoints.get(0)[0], rangePoints.get(1)[1]);
                RectImg.drawLine(rangePoints.get(1)[0], rangePoints.get(1)[1], rangePoints.get(1)[0], rangePoints.get(0)[1]);
            }
            if (drawPolygon) {
                for (int i = 0; i < rangePoints.size(); i++) {
                    if (i == rangePoints.size() - 1) {
                        RectImg.drawLine(rangePoints.get(i)[0], rangePoints.get(i)[1], rangePoints.get(0)[0], rangePoints.get(0)[1]);
                    } else {
                        RectImg.drawLine(rangePoints.get(i)[0], rangePoints.get(i)[1], rangePoints.get(i + 1)[0], rangePoints.get(i + 1)[1]);
                    }
                }
            }
        }
        RectImg.dispose();
        imageLabel.setIcon(new ImageIcon(drewImg));
        drawPolygon = false;
        drawRect = false;
    }

    private void onClickErase() {
        drewImg = deepCopy(toBufferedImage(scaledImg));
        imageLabel.setIcon(null);
        imageLabel.setIcon(new ImageIcon(scaledImg));

    }

    //I learned how to deep copy bufferedImage from: https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
    public static BufferedImage deepCopy(BufferedImage originImg) {
        ColorModel colorModel = originImg.getColorModel();
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        WritableRaster writableRaster = originImg.copyData(null);
        return new BufferedImage(colorModel, writableRaster, isAlphaPremultiplied, null);
    }

    private void onClickUpload(JFileChooser fc, JLabel imgName) {
        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            ImageIcon imageIcon = new ImageIcon(file.getPath());

            Image newimg = imageIcon.getImage();
            originImg = toBufferedImage(newimg);

            scaledImg = imageIcon.getImage().getScaledInstance(512, 512, java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImg);
            drewImg = deepCopy(toBufferedImage(scaledImg));
            imageLabel.setIcon(null);
            imageLabel.setIcon(new ImageIcon(scaledImg));
            imageLabel.setBounds(300, 50, 512, 512);
            add(imageLabel);
            imageLabel.setVisible(true);
            pack();
            setSize(1000, 800);

            imgName.setText("Image: " + file.getName());
            System.out.println("Opening: " + file.getName() + ".");
        }
    }

    private void imgLabelListner(MouseEvent me) {
        //draw points
        RectImg = drewImg.createGraphics();
        RectImg.setColor(Color.RED);
        RectImg.drawOval(me.getX(), me.getY(), 3, 3);
        RectImg.dispose();
        imageLabel.setIcon(new ImageIcon(drewImg));
        rangePoints.add(new int[]{me.getX(), me.getY()});

    }

    private static BufferedImage toBufferedImage(Image img) {

        BufferedImage newImg = new BufferedImage(img.getWidth(null), img.getWidth(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D background = newImg.createGraphics();
        background.drawImage(img, 0, 0, null);
        background.dispose();

        return newImg;
    }

}
