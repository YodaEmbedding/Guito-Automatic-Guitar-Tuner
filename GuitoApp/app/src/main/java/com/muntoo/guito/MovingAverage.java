package com.muntoo.guito;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;

/**
 * Created by Mateen on 2014-11-30.
 */
public class MovingAverage
{
	private int SIZE_MAX = 8;
	private Queue<Double> queue = new LinkedList<Double>();
	private Double average;
	private boolean resetflag = true;

	double getAverage()
	{
		if(resetflag == true)
		{
			calculateAverage();
		}

		return average;
	}

	void push(Double t)
	{
		if(queue.size() >= SIZE_MAX)
		{
			queue.remove();
		}

		queue.add(t);

		resetflag = true;
	}

	void calculateAverage()
	{
		if(queue.isEmpty())
		{
			average = -1.0;
			resetflag = false;
			return;
		}

		//access via Iterator
		Iterator<Double> it = queue.iterator();
		average = it.next();

		while(it.hasNext())
		{
			average += it.next();
		}

		average /= queue.size();

		resetflag = false;
	}
}
