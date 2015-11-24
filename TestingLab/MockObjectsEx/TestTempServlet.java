import junit.framework.*;
import com.mockobjects.servlet.*;

public class TestTempServlet extends TestCase {

  public TestTempServlet(String name){
  	super(name);
  }

  public void test_bad_parameter() throws Exception {
    TemperatureServlet s = new TemperatureServlet();
    MockHttpServletRequest request = 
      new MockHttpServletRequest();
    MockHttpServletResponse response = 
      new MockHttpServletResponse();
    
    request.setupAddParameter("Fahrenheit", "boo!");
    response.setExpectedContentType("text/html");
    s.doGet(request,response);
    response.verify();
    assertEquals("Invalid temperature: boo!\n",
                 response.getOutputStreamContents());
  }
  
  public void test_boil() throws Exception {
    TemperatureServlet s = new TemperatureServlet();
    MockHttpServletRequest request = 
      new MockHttpServletRequest();
    MockHttpServletResponse response = 
      new MockHttpServletResponse();
    
    request.setupAddParameter("Fahrenheit", "212");
    response.setExpectedContentType("text/html");
    s.doGet(request,response);
    response.verify();
    assertEquals("Fahrenheit: 212, Celsius: 100.0\n",
                 response.getOutputStreamContents());
  }

  public static void main(String args[]) {
  	String[] testCaseName = { TestTempServlet.class.getName() };
	junit.textui.TestRunner.main(testCaseName);
  }
  
}

