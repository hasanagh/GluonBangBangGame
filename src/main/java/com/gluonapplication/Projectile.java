package com.gluonapplication;

import java.util.ArrayList;

import javafx.scene.shape.Polyline;

/*
 * 
 * This class is responsible for the projectile motion of the ball 
 * 
 */

public class Projectile {

	private double theta, v0, mass, yInitial, xInitial, width, height;
	private double GRAVITY = 9.8;
	private double B = 0.8;
	private boolean player1Turn;
	private double secondY;

	public Projectile(double xInitial, double yInitial, double theta, double v0, double mass, boolean player1Turn,
			double width, double height, double secondy) {
		super();
		// Convert the given angle from degrees to radians
		this.theta = Math.toRadians(theta);
		this.v0 = v0;
		this.mass = mass;
		this.xInitial = xInitial;
		this.yInitial = yInitial;
		this.player1Turn = player1Turn;
		this.width = width;
		this.height = height;
		this.secondY = secondy;
	}

	private double[] getPoint(double t) {
		// getting x and y for a certain t using the x(t) and y(t) equations
		double function1 = ((mass * v0 * Math.cos(theta)) / B);
		double function2 = (1 - Math.exp((-B * t) / mass));
		double x = xInitial + function1 * function2;
		if (!player1Turn) {
			x = x - 2 * (x - xInitial);
		}
		double y = yInitial
				- ((mass / GRAVITY) * (((mass * GRAVITY) / B) + v0 * Math.sin(theta)) * (1 - Math.exp((-B * t) / mass))
						- (mass * GRAVITY * t) / B);

		double[] arr = { x, y };

		return arr;
	}

	Polyline getPolyLine(int accuracy) {
		ArrayList<Double> arrList = new ArrayList<>();
		double maxy = height;
		// getting all points forming the projectile with accuracy
		for (double t = 0; t < accuracy - 1; t = t + 0.1) {
			double[] pt = getPoint(t);
			arrList.add(pt[0]);
			arrList.add(pt[1]);

			// get maximum height
			if (pt[1] < maxy) {
				maxy = pt[1];
			}

			// if ground reached after the maximum is reached => break
			if (player1Turn) {
				if (pt[0] <= width / 4) {
					if (pt[1] > maxy && pt[1] >= yInitial) {
						break;
					}
				} else {
					if (pt[1] > maxy && pt[1] >= secondY) {
						break;
					}
				}
			} else {
				if (pt[0] <= width / 4) {
					if (pt[1] > maxy && pt[1] >= yInitial) {
						break;
					}
				} else {
					if (pt[1] > maxy && pt[1] >= secondY) {
						break;
					}
				}
			}
		}
		Double[] points = arrList.toArray(new Double[arrList.size()]);
		Polyline polyline = new Polyline();
		polyline.getPoints().addAll(points);
		return polyline;
	}

}
