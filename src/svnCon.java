import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


public class svnCon {
	
	SVNRepository repository=null;
	SVNProperties fileProperties = new SVNProperties();
	ByteArrayOutputStream data = new ByteArrayOutputStream();
	String filePath="";
	
	svnCon(String type)
	{
		if(type=="DAV" || type=="dav") //for http:// https://
			DAVRepositoryFactory.setup();
		else if(type=="SVN" || type=="svn") //for svn://
			SVNRepositoryFactoryImpl.setup();
		else if(type=="FS" || type=="fs") //for file:///
			FSRepositoryFactory.setup();
		else if(type=="all" || type=="ALL") //for all
		{
			DAVRepositoryFactory.setup();
			SVNRepositoryFactoryImpl.setup();
			FSRepositoryFactory.setup();
		}else
		{
			System.out.println("[Error] Configuration Not Recognized");
		}
	}
	
	
	void createRepository(String url)
	{
		try{
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
			
		}catch(SVNException svne)
		{
			System.out.println("[Error] Creating Repository Failed");
		}
	}
	
	
	SVNRepository getRepository()
	{
		if(repository!=null)
			return repository;
		else
		{
			System.out.println("[Error] Repository not created.");
			return null;
		}
	}
	
	
	boolean authenticateRepository(String username,String password)
	{
		ISVNAuthenticationManager authManager=SVNWCUtil.createDefaultAuthenticationManager(username,password);
		
		repository.setAuthenticationManager(authManager);
		
		return true;
	}
	
	
	void processFile(String filePath,int revisionNumber)
	{
		try
		{
			SVNNodeKind nodeKind = repository.checkPath(filePath, revisionNumber);
			if(!isPathEmpty(nodeKind) && !isPathFolder(nodeKind))
			{
				repository.getFile(filePath,revisionNumber,fileProperties,data);
			}else
			{
				System.out.println("[Error] Path doesn't point to a file");
			}
			
		}catch(SVNException svne)
		{
			System.out.println("[Error] Processing path failed");
		}
	}
	
	//default revisionNumber -1
	Map<String,String> getFileProperty(int revisionNumber)
	{
		Map<String,String> data = new HashMap<String,String>();
		
		if(fileProperties.isEmpty())
		{
			System.out.println("[Error] File Property not initialized, used processFile procedure.");
		}
		
		Iterator iterator = fileProperties.nameSet().iterator();
		while(iterator.hasNext())
		{
			String propertyName = (String) iterator.next();
			String propertyValue = (String) fileProperties.getStringValue(propertyName);
			
			data.put(propertyName, propertyValue);
		}
		
		return data;
		
	}
	
	//default revisionNumber -1
	ByteArrayOutputStream getFileData(int revisionNumber)
	{
		if(data.size()==0)
		{
			System.out.println("[Error] File Data not initialized, used processFile procedure.");
		}
		
		return data;
	}
	
	
	void displayData()
	{
		try
		{
			if(data.size() == 0)
			{
				System.out.println("[Error] File Data not initialized, used processFile procedure.");
			}
			
			data.writeTo(System.out);
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	
	long getLatestRevision()
	{
		long latestRevision = -1;
		try
		{
			latestRevision = repository.getLatestRevision();
		}catch(SVNException svne)
		{
			System.out.println("[Error] Fetching Latest Revision Failed");
		}
		return latestRevision;
	}
	
	
	boolean isPathEmpty(SVNNodeKind nodeKind)
	{
		if(nodeKind == SVNNodeKind.NONE)
		{
			return true;
		}
		
		return false;
	}
	
	
	boolean isPathFolder(SVNNodeKind nodeKind)
	{
		if(nodeKind == SVNNodeKind.DIR)
		{
			return true;
		}
		
		return false;
	}
	
	
	boolean isFileReadable()
	{
		if(fileProperties.isEmpty())
		{
			System.out.println("[Error] File Property not initialized, used processFile procedure.");
		}
		String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
		
		return SVNProperty.isTextMimeType(mimeType);
	}
	
}
