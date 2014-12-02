package com.muntoo.guito;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;

/**
 * Created by Mateen on 2014-11-30.
 */
public class MovingAverage
{
	private int SIZE_MAX = 16;
	private Queue<Integer> queue = new LinkedList<Integer>();
	private Object[] sorted;
	private Integer average;
	private boolean resetflag = true;

	int getAverage()
	{
		if(resetflag == true)
		{
			// calculateMean();
			calculateMedian();
		}

		return average;
	}

	void push(Integer t)
	{
		if(queue.size() >= SIZE_MAX)
		{
			queue.remove();
		}

		queue.add(t);

		// average = t;

		resetflag = true;
	}

	void calculateMean()
	{
		if(queue.isEmpty())
		{
			average = -1;
			resetflag = false;
			return;
		}

		int n = 0;
		Integer i = -1;

		//access via Iterator
		Iterator<Integer> it = queue.iterator();

		average = 0;

		while(it.hasNext())
		{
			i = it.next();

			if(i == -1)
			{
				continue;
			}

			average += i;
			++n;
		}

		if(n == 0)
			average = -1;
		else
			average = average / n;

		resetflag = false;
	}

	public void calculateMedian()
	{
		if(queue.isEmpty())
		{
			average = -1;
			resetflag = false;
			return;
		}

		sorted = queue.toArray();
		Arrays.sort(sorted);

		average = (Integer)sorted[sorted.length / 2];

		// If median is -1, cut off first half of the values and find median of second half
		if(average == -1)
			average = (Integer)sorted[sorted.length * 3 / 4];

		resetflag = false;
	}
}
