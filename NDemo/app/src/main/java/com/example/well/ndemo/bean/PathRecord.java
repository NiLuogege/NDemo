package com.example.well.ndemo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于记录一条轨迹，包括起点、终点、轨迹中间点、距离、耗时、平均速度、时间
 * 
 * @author ligen
 * 
 */
public class PathRecord {
	private NodemoMapLocation mStartPoint;
	private NodemoMapLocation mEndPoint;
	private List<NodemoMapLocation> mPathLinePoints = new ArrayList<NodemoMapLocation>();

	public List<Float> getSpeedList() {
		return mSpeedList;
	}

	public void setSpeedList(List<Float> speedList) {
		mSpeedList = speedList;
	}

	private List<Float> mSpeedList = new ArrayList<Float>();
	private String mDistance;
	private String mDuration;
	private String mAveragespeed;
	private String mDate;
	private int mId = 0;

	public PathRecord() {

	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public NodemoMapLocation getStartPoint() {
		return mStartPoint;
	}

	public void setStartPoint(NodemoMapLocation startPoint) {
		mStartPoint = startPoint;
	}

	public NodemoMapLocation getEndPoint() {
		return mEndPoint;
	}

	public void setEndPoint(NodemoMapLocation endPoint) {
		mEndPoint = endPoint;
	}

	public List<NodemoMapLocation> getPathline() {
		return mPathLinePoints;
	}

	public void setPathline(List<NodemoMapLocation> pathline) {
		this.mPathLinePoints = pathline;
	}

	public String getDistance() {
		return mDistance;
	}

	public void setDistance(String distance) {
		this.mDistance = distance;
	}

	public String getDuration() {
		return mDuration;
	}

	public void setDuration(String duration) {
		this.mDuration = duration;
	}

	public String getAveragespeed() {
		return mAveragespeed;
	}

	public void setAveragespeed(String averagespeed) {
		this.mAveragespeed = averagespeed;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		this.mDate = date;
	}

	public void addpoint(NodemoMapLocation point) {
		mPathLinePoints.add(point);
	}

	@Override
	public String toString() {
		StringBuilder record = new StringBuilder();
		record.append("recordSize:" + getPathline().size() + ", ");
		record.append("distance:" + getDistance() + "m, ");
		record.append("duration:" + getDuration() + "s");
		return record.toString();
	}
}
