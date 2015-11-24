import junit.framework.TestCase;

public class RationalTest extends TestCase {

    protected Rational HALF;

    protected void setUp() {
      HALF = new Rational( 1, 2 );
    }

    // Create new test
    public RationalTest (String name) {
        super(name);
    }

    public void testEquality() {
        assertEquals(new Rational(1,3), new Rational(1,3));
        assertEquals(new Rational(1,3), new Rational(2,6));
        assertEquals(new Rational(3,3), new Rational(1,1));
    }

    // Test for nonequality
    public void testNonEquality() {
	assertFalse(new Rational(2,3).equals(null));
	assertFalse(new Rational(2,3).equals(new Integer(1)));
        assertFalse(new Rational(2,3).equals(
            new Rational(1,3)));
	assertFalse(new Rational(-1,3).equals(
	    new Rational(-1,-2)));
    }

    public void testAccessors() {
    	assertEquals(new Rational(2,3).numerator(), 2);
    	assertEquals(new Rational(2,3).denominator(), 3);
    }

    public void testPlus(){
	assertEquals(new Rational(2,3).plus(new Rational(2,3)), 
		new Rational(4,3));
	assertEquals(new Rational(4,9).plus(new Rational(7,2)),
                new Rational(71,18));

    }

    public void testMinus(){
	assertEquals(new Rational(2,3).minus(new Rational(1,3)), 
		new Rational(1,3));
	assertEquals(new Rational(71,19).minus(new Rational(5,12)),
                new Rational(757,228));
	assertEquals(new Rational(1,4).minus(new Rational(3,4)),
		new Rational(-1,2));
	assertEquals(new Rational(1,4).minus(new Rational(3,4)),
                new Rational(1,-2));
    }

    public void testTimes(){
	assertEquals(new Rational(2,3).times(new Rational(2,3)), 
		new Rational(4,9));
	assertEquals(new Rational(-7,8).times(new Rational(-8,7)),
		new Rational(1,1));
	assertEquals(new Rational(-1,90).times(new Rational(4,2)),
		new Rational(-1,45));
    }

    public void testDivides(){
	assertEquals(new Rational(2,3).divides(new Rational(2,3)), 
		new Rational(1,1));
	assertEquals(new Rational(-1,90).divides(new Rational(4,2)),
		new Rational(-1,180));
    }

    public static void testTolerance(){
	Rational s = new Rational(1,3);
	s.setTolerance(new Rational(1,1000));
	assertEquals(s.getTolerance(),new Rational(1,1000));
    }

    public void testAbs(){
	Rational s = new Rational(new Rational(-2,3));
	assertEquals(s.abs(), new Rational(2,3));
	s = new Rational(2,-3);
	assertEquals(s.abs(), new Rational(2,3));
	s = new Rational(-2,-3);
	assertEquals(s.abs(), new Rational(2,3));
    }

    public void testLessThan(){
	assertTrue(new Rational(1,4).isLessThan(new Rational(1,2)));
    }
    
    public void testRoot() {
        Rational s = new Rational( 1, 4 );
        Rational sRoot = null;
        try {
            sRoot = s.root();
        } catch (IllegalArgumentToSquareRootException e) {
            e.printStackTrace();
        }
        assertTrue( sRoot.isLessThan( HALF.plus( Rational.getTolerance() ) ) 
                        && HALF.minus( Rational.getTolerance() ).isLessThan( sRoot ) );
    }

    public static void main(String args[]) {
        String[] testCaseName = 
            { RationalTest.class.getName() };
        // junit.swingui.TestRunner.main(testCaseName);
        junit.textui.TestRunner.main(testCaseName);
    }
}
