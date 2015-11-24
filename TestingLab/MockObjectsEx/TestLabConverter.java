import junit.framework.*;
import com.mockobjects.servlet.*;

public class TestLabConverter extends TestCase {
    public TestLabConverter(String name){
	super(name);
    }

    public void test_validTemp() throws Exception {
	TestingLabConverterServlet s = new TestingLabConverterServlet();
	MockHttpServletRequest request = 
	    new MockHttpServletRequest();
	MockHttpServletResponse response =
	    new MockHttpServletResponse();
	
	request.setupAddParameter("farenheitTemperature","80");
	response.setExpectedContentType("text/html");
	s.doGet(request,response);
	response.verify();
	assertEquals("<html><head><title>Temperature Converter Result</title></head><body><h2>80 Farenheit = 26.67 Celsius </h2>\n<p><h3>The temperature in Austin is 451 degrees Farenheit</h3>\n</body></html>\n",response.getOutputStreamContents());
    }
	
    public void test_nullTemp() throws Exception {
        TestingLabConverterServlet s = new TestingLabConverterServlet();
        MockHttpServletRequest request =
            new MockHttpServletRequest();
        MockHttpServletResponse response =
            new MockHttpServletResponse();
	
	String nullString = null;
	request.setupAddParameter("farenheitTemperature",nullString);
        response.setExpectedContentType("text/html");
        s.doGet(request,response);
        response.verify();
	assertEquals("<html><head><title>No Temperature</title></head><body><h2>Need to enter a temperature!</h2></body></html>\n",response.getOutputStreamContents());

    }

    public void test_invalidTemp() throws Exception {
        TestingLabConverterServlet s = new TestingLabConverterServlet();
        MockHttpServletRequest request =
            new MockHttpServletRequest();
        MockHttpServletResponse response =
            new MockHttpServletResponse();

        request.setupAddParameter("farenheitTemperature","bob");
        response.setExpectedContentType("text/html");
        s.doGet(request,response);
        response.verify();
	assertEquals("<html><head><title>Bad Temperature</title></head><body><h2>Need to enter a valid temperature!Got a NumberFormatException on bob</h2></body></html>\n",response.getOutputStreamContents());        

    }


    public static void main(String args[]){
        String[] testCaseName = { TestLabConverter.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
}
