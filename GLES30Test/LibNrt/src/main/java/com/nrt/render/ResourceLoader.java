package com.nrt.render;

import java.util.*;
import java.util.concurrent.*;

import android.app.job.*;

import com.nrt.basic.TextViewLog;
import com.nrt.basic.*;

import com.nrt.framework.Subsystem;

public class ResourceLoader
{
	static class JobItem
	{
		public String Name = null;
		public ResourceQueue ResourceQueue = null;
		public Job Job = null;		
		public JobItem( String name, ResourceQueue drq, Job job )
		{
			Name = name;
			ResourceQueue = drq;
			Job = job;
		}
	}

	public interface Job
	{
		public void OnLoadContent( ResourceQueue drq );
	}

	Queue<JobItem> m_queueJobs = new ConcurrentLinkedQueue<JobItem>();
	List<JobItem> m_listJobs = new ArrayList<JobItem>();
	public TextViewLog m_log = null;		

	class LoaderThread extends Thread
	{		
		public LoaderThread( ThreadGroup threadGroup, String strName )
		{
			super( threadGroup, strName );
		}

		@Override
		public void run()
		{			
			for(;;)
			{		
				while( 0 < m_queueJobs.size() )
				{
					JobItem jobItem = m_queueJobs.poll();
					m_log.WriteLine( String.format("job %s start", jobItem.Name ) );
					jobItem.Job.OnLoadContent( jobItem.ResourceQueue );
					m_log.WriteLine( String.format("job %s end", jobItem.Name ) );

					Thread.yield();
				}

				if( isInterrupted() )
				{
					break;
				}
				Thread.yield();
			}

			m_log.WriteLine(this.getName() + " thread end.");
			super.run();
		}		
	}

	ThreadGroup m_threadGroup = new ThreadGroup( "delay loader thread group" );
	LoaderThread[] m_loaderThreads = null;

	public ResourceLoader( int nbThreads, TextViewLog log )
	{
		m_log = log;
		m_loaderThreads = new LoaderThread[nbThreads];
		for( int i = 0 ; i < m_loaderThreads.length ; i++ )
		{
			m_loaderThreads[i] = new LoaderThread( m_threadGroup, "loader"+i );
			m_loaderThreads[i].start();
		}
	}

	public void RegisterJob( String strName, ResourceQueue drq, Job job )
	{
		JobItem jobItem = new JobItem( strName, drq, job );
		m_queueJobs.offer( jobItem );
		m_listJobs.add( jobItem );
	}

	public int GetAllJobCount()
	{
		return m_listJobs.size();
	}

	public int GetLeftJobCount()
	{
		return m_queueJobs.size();
	}
}

