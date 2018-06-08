

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Servlet Filter implementation class SearchServletLoggingFilter
 */
@WebFilter(filterName = "SearchServletLoggingFilter", urlPatterns = "/SearchServlet")
public class SearchServletLoggingFilter implements Filter {

    /**
     * Default constructor. 
     */
    public SearchServletLoggingFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
    	// Used for logging information
    	long startTimeTS = System.nanoTime();
		chain.doFilter(request, response);
		long endTimeTS = System.nanoTime();
        long elapsedTimeTS = endTimeTS - startTimeTS; // elapsed time in nano seconds. Note: print the values in nano seconds
        appendToLogFile(elapsedTimeTS, "TSLog.mat", request);
	}

	private void appendToLogFile(long elapsedTime, String fileName, ServletRequest request)
	{
		String contextPath = request.getServletContext().getRealPath("");
		String filePath=contextPath + fileName;
		System.out.println(filePath);
		File myfile = new File(filePath);
		try
		{
			myfile.createNewFile();
			FileWriter writer = new FileWriter(myfile, true);
			writer.append(elapsedTime + "\n");
			writer.close();
		}
		catch (Exception e)
		{
			System.out.print("File failed to write");
		}
	}
	
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
