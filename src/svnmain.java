public class svnmain
{
	public static void main(String[] args)
	{
		//complete configuration
		svnCon svncon = new svnCon("all");
		
		//Trying to read the SVN trunk README file
		String url="http://svn.svnkit.com/repos/svnkit/trunk";
		String filePath="README.txt";
		
		//creating the repository
		svncon.createRepository(url);
		
		//Repository used is public
		svncon.authenticateRepository("", "");
		
		//Processing the datas
		svncon.processFile(filePath, -1);
		
		//Displaying the content
		svncon.displayData();
		
	}
}