package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class Hack extends Thread {

    private Controller controller;
    public boolean isStop;

    private static Random RANDOM = new Random();

    private static final float RATIO = 3.72f;

    public Hack(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        String root = Hack.class.getResource("/").getPath();
        String adbPath = root + "/platform-tools/adb";
        File srcDir = new File(root, "imgs/input");
        srcDir.mkdirs();

        MyPosFinder myPosFinder = new MyPosFinder();
        NextCenterFinder nextCenterFinder = new NextCenterFinder();

        int count = 0;
        while (!isStop) {

            count++;
            try {
                File file = new File(srcDir, count + ".png");
                if (file.exists()) {
                    file.deleteOnExit();
                }

                controller.appendText("正在拉取截图...\n");

                Process process = Runtime.getRuntime().exec(adbPath + " shell /system/bin/screencap -p /sdcard/screenshot.png");
                process.waitFor();
                process = Runtime.getRuntime().exec(adbPath + " pull /sdcard/screenshot.png " + file.getAbsolutePath());
                process.waitFor();


                if (file.exists()) {
                    controller.appendText("截图获取成功.\n");

                    BufferedImage img = ImgLoader.load(file.getAbsolutePath());

                    controller.appendText("计算人物中心点...\n");

                    int[] myPos = myPosFinder.find(img);
                    BufferedImage desc = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                    desc.getGraphics().drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null); // 绘制缩小后的图
                    Graphics gPaint = desc.getGraphics();
                    gPaint.setColor(Color.RED);
                    gPaint.fillRect(myPos[0] - 5, myPos[1] - 5, 10, 10);

                    controller.appendText("人物中心点[红色]:[" + myPos[0] + "," + myPos[1] + "]\n");

                    controller.appendText("计算下一步中心点...\n");
                    int[] nextPos = nextCenterFinder.find(img, myPos);
                    gPaint.setColor(Color.GREEN);
                    gPaint.fillRect(nextPos[0] - 5, nextPos[1] - 5, 10, 10);
                    gPaint.fillRect(nextPos[2] - 5, nextPos[3] - 5, 10, 10);
                    gPaint.fillRect(nextPos[4] - 5, nextPos[5] - 5, 10, 10);

                    int centerX = (nextPos[2] + nextPos[4]) / 2;
                    int centerY = (nextPos[3] + nextPos[5]) / 2;

                    if (centerX <= 0 || centerY <= 0) {
                        centerX = nextPos[0];
                        centerY = nextPos[1] + 36;
                    }
                    gPaint.setColor(Color.MAGENTA);
                    gPaint.fillRect(centerX - 5, centerY - 5, 10, 10);

                    gPaint.setColor(Color.ORANGE);
                    gPaint.drawLine(myPos[0], myPos[1], centerX, centerY);

                    Image tempImg = SwingFXUtils.toFXImage(desc, null);
                    controller.setImage(tempImg);

                    controller.appendText("下一步中心点[品红]:[" + centerX + "," + centerY + "]\n");

                    double distance = Math.sqrt((centerX - myPos[0]) * (centerX - myPos[0]) + (centerY - myPos[1]) * (centerY - myPos[1]));
                    controller.appendText("距离:" + (int) distance + "像素\n");
                    int pressX = 400 + RANDOM.nextInt(100);
                    int pressY = 500 + RANDOM.nextInt(100);

                    int time = (int) (Math.pow(distance, 0.85) * RATIO);

                    controller.appendText("按压系数=" + RATIO + "，时间=" + time + "ms\n");

                    String adbCommand = adbPath + String.format(" shell input swipe %d %d %d %d %d", pressX, pressY, pressX, pressY, time);
                    Runtime.getRuntime().exec(adbCommand);
                }
                int sleepTime = 3000 + RANDOM.nextInt(1000);
                controller.appendText("等待" + sleepTime + "ms后继续\n");
                if (isStop) {
                    break;
                }
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        controller.appendText("已停止!\n");
        controller.setStopped();
    }
}
