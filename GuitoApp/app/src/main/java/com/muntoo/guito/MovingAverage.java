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
	private Queue<Integer> queue = new LinkedList<Integer>();
	private Integer average;
	private boolean resetflag = true;

	int getAverage()
	{
		if(resetflag == true)
		{
			calculateAverage();
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

	void calculateAverage()
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
}
