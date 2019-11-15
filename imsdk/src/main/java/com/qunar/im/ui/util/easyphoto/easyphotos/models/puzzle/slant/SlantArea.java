package com.qunar.im.ui.util.easyphoto.easyphotos.models.puzzle.slant;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;


import com.qunar.im.ui.util.easyphoto.easyphotos.models.puzzle.Area;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.puzzle.Line;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * @author wupanjie
 */
class SlantArea implements Area {
  SlantLine lineLeft;
  SlantLine lineTop;
  SlantLine lineRight;
  SlantLine lineBottom;

  CrossoverPointF leftTop;
  CrossoverPointF leftBottom;
  CrossoverPointF rightTop;
  CrossoverPointF rightBottom;

  private PointF tempPoint;

  private float paddingLeft;
  private float paddingTop;
  private float paddingRight;
  private float paddingBottom;
  private float radian;

  private Path areaPath = new Path();
  private RectF areaRect = new RectF();
  private PointF[] handleBarPoints = new PointF[2];

  SlantArea() {
    handleBarPoints[0] = new PointF();
    handleBarPoints[1] = new PointF();

    leftTop = new CrossoverPointF();
    leftBottom = new CrossoverPointF();
    rightTop = new CrossoverPointF();
    rightBottom = new CrossoverPointF();

    tempPoint = new PointF();
  }

  SlantArea(SlantArea src) {
    this();
    this.lineLeft = src.lineLeft;
    this.lineTop = src.lineTop;
    this.lineRight = src.lineRight;
    this.lineBottom = src.lineBottom;

    this.leftTop = src.leftTop;
    this.leftBottom = src.leftBottom;
    this.rightTop = src.rightTop;
    this.rightBottom = src.rightBottom;

    updateCornerPoints();
  }

  @Override
  public float left() {
    return Math.min(leftTop.x, leftBottom.x) + paddingLeft;
  }

  @Override
  public float top() {
    return Math.min(leftTop.y, rightTop.y) + paddingTop;
  }

  @Override
  public float right() {
    return Math.max(rightTop.x, rightBottom.x) - paddingRight;
  }

  @Override
  public float bottom() {
    return Math.max(leftBottom.y, rightBottom.y) - paddingBottom;
  }

  @Override
  public float centerX() {
    return (left() + right()) / 2;
  }

  @Override
  public float centerY() {
    return (top() + bottom()) / 2;
  }

  @Override
  public float width() {
    return right() - left();
  }

  @Override
  public float height() {
    return bottom() - top();
  }

  @Override
  public PointF getCenterPoint() {
    return new PointF(centerX(), centerY());
  }

  public Path getAreaPath() {
    areaPath.reset();

    if (radian > 0) {
      float tempRatio = radian / SlantUtils.distance(leftTop, leftBottom);
      SlantUtils.getPoint(tempPoint, leftTop, leftBottom, Line.Direction.VERTICAL, tempRatio);
      tempPoint.offset(paddingLeft, paddingTop);
      areaPath.moveTo(tempPoint.x, tempPoint.y);

      tempRatio = radian / SlantUtils.distance(leftTop, rightTop);
      SlantUtils.getPoint(tempPoint, leftTop, rightTop, Line.Direction.HORIZONTAL, tempRatio);
      tempPoint.offset(paddingLeft, paddingTop);
      areaPath.quadTo(leftTop.x + paddingLeft, leftTop.y + paddingTop, tempPoint.x, tempPoint.y);

      tempRatio = 1 - tempRatio;
      SlantUtils.getPoint(tempPoint, leftTop, rightTop, Line.Direction.HORIZONTAL, tempRatio);
      tempPoint.offset(-paddingRight, paddingTop);
      areaPath.lineTo(tempPoint.x, tempPoint.y);

      tempRatio = radian / SlantUtils.distance(rightTop, rightBottom);
      SlantUtils.getPoint(tempPoint, rightTop, rightBottom, Line.Direction.VERTICAL, tempRatio);
      tempPoint.offset(-paddingRight, paddingTop);
      areaPath.quadTo(rightTop.x - paddingLeft, rightTop.y + paddingTop, tempPoint.x, tempPoint.y);

      tempRatio = 1 - tempRatio;
      SlantUtils.getPoint(tempPoint, rightTop, rightBottom, Line.Direction.VERTICAL, tempRatio);
      tempPoint.offset(-paddingRight, -paddingBottom);
      areaPath.lineTo(tempPoint.x, tempPoint.y);

      tempRatio = 1 - radian / SlantUtils.distance(leftBottom, rightBottom);
      SlantUtils.getPoint(tempPoint, leftBottom, rightBottom, Line.Direction.HORIZONTAL, tempRatio);
      tempPoint.offset(-paddingRight, -paddingBottom);
      areaPath.quadTo(rightBottom.x - paddingRight, rightBottom.y - paddingTop, tempPoint.x, tempPoint.y);

      tempRatio = 1 - tempRatio;
      SlantUtils.getPoint(tempPoint, leftBottom, rightBottom, Line.Direction.HORIZONTAL, tempRatio);
      tempPoint.offset(paddingLeft, -paddingBottom);
      areaPath.lineTo(tempPoint.x, tempPoint.y);

      tempRatio = 1 - radian / SlantUtils.distance(leftTop, leftBottom);
      SlantUtils.getPoint(tempPoint, leftTop, leftBottom, Line.Direction.VERTICAL, tempRatio);
      tempPoint.offset(paddingLeft, -paddingBottom);
      areaPath.quadTo(leftBottom.x + paddingLeft, leftBottom.y - paddingBottom, tempPoint.x, tempPoint.y);

      tempRatio = 1 - tempRatio;
      SlantUtils.getPoint(tempPoint, leftTop, leftBottom, Line.Direction.VERTICAL, tempRatio);
      tempPoint.offset(paddingLeft, paddingTop);
      areaPath.lineTo(tempPoint.x, tempPoint.y);
    } else {
      areaPath.moveTo(leftTop.x + paddingLeft, leftTop.y + paddingTop);
      areaPath.lineTo(rightTop.x - paddingRight, rightTop.y + paddingTop);
      areaPath.lineTo(rightBottom.x - paddingRight, rightBottom.y - paddingBottom);
      areaPath.lineTo(leftBottom.x + paddingLeft, leftBottom.y - paddingBottom);
      areaPath.lineTo(leftTop.x + paddingLeft, leftTop.y + paddingTop);
    }
    return areaPath;
  }

  @Override
  public RectF getAreaRect() {
    areaRect.set(left(), top(), right(), bottom());
    return areaRect;
  }

  public boolean contains(float x, float y) {
    return SlantUtils.contains(this, x, y);
  }

  @Override
  public boolean contains(Line line) {
    return lineLeft == line || lineTop == line || lineRight == line || lineBottom == line;
  }

  @Override
  public boolean contains(PointF point) {
    return contains(point.x, point.y);
  }

  @Override
  public List<Line> getLines() {
    return Arrays.asList((Line) lineLeft, lineTop, lineRight, lineBottom);
  }

  @Override
  public PointF[] getHandleBarPoints(Line line) {
    if (line == lineLeft) {
      SlantUtils.getPoint(handleBarPoints[0], leftTop, leftBottom, line.direction(), 0.25f);
      SlantUtils.getPoint(handleBarPoints[1], leftTop, leftBottom, line.direction(), 0.75f);
      handleBarPoints[0].offset(paddingLeft, 0);
      handleBarPoints[1].offset(paddingLeft, 0);
    } else if (line == lineTop) {
      SlantUtils.getPoint(handleBarPoints[0], leftTop, rightTop, line.direction(), 0.25f);
      SlantUtils.getPoint(handleBarPoints[1], leftTop, rightTop, line.direction(), 0.75f);
      handleBarPoints[0].offset(0, paddingTop);
      handleBarPoints[1].offset(0, paddingTop);
    } else if (line == lineRight) {
      SlantUtils.getPoint(handleBarPoints[0], rightTop, rightBottom, line.direction(), 0.25f);
      SlantUtils.getPoint(handleBarPoints[1], rightTop, rightBottom, line.direction(), 0.75f);
      handleBarPoints[0].offset(-paddingRight, 0);
      handleBarPoints[1].offset(-paddingRight, 0);
    } else if (line == lineBottom) {
      SlantUtils.getPoint(handleBarPoints[0], leftBottom, rightBottom, line.direction(), 0.25f);
      SlantUtils.getPoint(handleBarPoints[1], leftBottom, rightBottom, line.direction(), 0.75f);
      handleBarPoints[0].offset(0, -paddingBottom);
      handleBarPoints[1].offset(0, -paddingBottom);
    }
    return handleBarPoints;
  }

  @Override
  public float radian() {
    return radian;
  }

  @Override
  public void setRadian(float radian) {
    this.radian = radian;
  }

  @Override
  public float getPaddingLeft() {
    return paddingLeft;
  }

  @Override
  public float getPaddingTop() {
    return paddingTop;
  }

  @Override
  public float getPaddingRight() {
    return paddingRight;
  }

  @Override
  public float getPaddingBottom() {
    return paddingBottom;
  }

  @Override
  public void setPadding(float padding) {
    setPadding(padding, padding, padding, padding);
  }

  @Override
  public void setPadding(float paddingLeft, float paddingTop, float paddingRight, float paddingBottom) {
    this.paddingLeft = paddingLeft;
    this.paddingTop = paddingTop;
    this.paddingRight = paddingRight;
    this.paddingBottom = paddingBottom;
  }

  void updateCornerPoints() {
    SlantUtils.intersectionOfLines(leftTop, lineLeft, lineTop);
    SlantUtils.intersectionOfLines(leftBottom, lineLeft, lineBottom);
    SlantUtils.intersectionOfLines(rightTop, lineRight, lineTop);
    SlantUtils.intersectionOfLines(rightBottom, lineRight, lineBottom);
  }

  static class AreaComparator implements Comparator<SlantArea> {

    @Override
    public int compare(SlantArea one, SlantArea two) {
      if (one.leftTop.y < two.leftTop.y) {
        return -1;
      } else if (one.leftTop.y == two.leftTop.y) {
        if (one.leftTop.x < two.leftTop.x) {
          return -1;
        } else {
          return 1;
        }
      } else {
        return 1;
      }
    }
  }
}