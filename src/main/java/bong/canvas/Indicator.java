package bong.canvas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Indicator {
  protected float centerX, centerY, size;

  public Indicator(float centerX, float centerY, float size){
    this.centerX = centerX;
    this.centerY = centerY;
    this.size = size;
  }

  public float getCenterX() {
    return centerX;
}

  public float getCenterY() {
      return centerY;
  }

  protected String circlePath(float cx, float cy, float r) {
    return "M " + cx + " " + cy + " m -" + r + ", 0 a " + r + "," + r + " 0 1,0 " + (r * 2) + ",0 a " + r + "," + r + " 0 1,0 -" + (r * 2) + ",0";
  }

  public String scaleSvgPath(String text, double factor) {
    String out = "";
    String regex = "([Mmcla]{1}[0-9,\\-\\.]+)|(z)";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);

    while (matcher.find()) {
        String str = matcher.group();
        String c = str.substring(0,1);

        String regex2 = "(-?\\d+\\.?\\d*)";
        Pattern pattern2 = Pattern.compile(regex2);
        Matcher matcher2 = pattern2.matcher(str);

        String out1 = c;
        while (matcher2.find()) {
            double d = Double.parseDouble(matcher2.group());
            d = d * factor;
            out1 += d + " ";
        }
        out += out1;
    }

    return out;
}
}