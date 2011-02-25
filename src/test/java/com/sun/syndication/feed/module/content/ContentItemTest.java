/*
 * ContentItemTest.java
 * JUnit based test
 *
 * Created on February 2, 2005, 2:50 PM
 */

package com.sun.syndication.feed.module.content;

import junit.framework.TestCase;

/**
 * This is all standard property storage testing.
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert Cooper</a>
 */
public class ContentItemTest extends TestCase {
    
    private ContentItem item = new ContentItem();
    
    public ContentItemTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(ContentItemTest.class);
        
        return suite;
    }

    /**
     * Test of ContentFormat method, of class com.totsp.xml.syndication.content.ContentItem.
     */
    public void testContentFormat() {
        System.out.println("testContentFormat");        
        String test = "application/xhtml";                        
        item.setContentFormat(test);
        assertTrue( item.getContentFormat().equals( test ));
    }


    /**
     * Test of ContentEncoding method, of class com.totsp.xml.syndication.content.ContentItem.
     */
    public void testContentEncoding() {
        System.out.println("testContentEncoding");        
        String test = "http://www.w3.org/TR/REC-xml#dt-wellformed";
                        
        item.setContentFormat(test);
        assertTrue( item.getContentFormat().equals( test ));
    }


    /**
     * Test of ContentValue method, of class com.totsp.xml.syndication.content.ContentItem.
     */
    public void testContentValue() {
        System.out.println("testContentValue");
        String test = "<em>This is<strong>very</em> cool</strong>.";
                        
        item.setContentFormat(test);
        assertTrue( item.getContentFormat().equals( test ));
    }

    
    /**
     * Test of ContentAbout method, of class com.totsp.xml.syndication.content.ContentItem.
     */
    public void testContentAbout() {
        System.out.println("testContentAbout");
        String test = "http://example.org/item/content.svg";
                        
        item.setContentFormat(test);
        assertTrue( item.getContentFormat().equals( test ));
    }

    
    /**
     * Test of ContentValueParseType method, of class com.totsp.xml.syndication.content.ContentItem.
     */
    public void testContentValueParseType() {
        System.out.println("testContentValueParseType");
        String test = "Literal";
                        
        item.setContentFormat(test);
        assertTrue( item.getContentFormat().equals( test ));
    }

    

    /**
     * Test of ContentValueNamespace method, of class com.totsp.xml.syndication.content.ContentItem.
     */
    public void testContentValueNamespace() {
        System.out.println("testContentValueNamespace");
        String test = "http://www.w3.org/1999/xhtml";
                        
        item.setContentFormat(test);
        assertTrue( item.getContentFormat().equals( test ));
    }

    /**
     * Test of ContentResource method, of class com.totsp.xml.syndication.content.ContentItem.
     */
    public void testContentResource() {
        System.out.println("testContentResource");
        String test = "http://www.w3.org/2000/svg";
                        
        item.setContentResource(test);
        assertTrue( item.getContentResource().equals( test ));
    }
   
    
    /** this is a stupid test.
     * @todo make better
     * Test of ContentResource method, of class com.totsp.xml.syndication.content.ContentItem.
     */
    public void testEquals() {
        System.out.println("testEquals");
        String test = "http://www.w3.org/2000/svg";
                        
        
        assertTrue( test.equals( test ) );
        ContentItem test2 = (ContentItem) ContentModuleImplTest.contentItems.get(0);
        assertTrue( !test.equals( test2) );
        ContentItem test3 = new ContentItem();
        test3.setContentFormat(new String("http://www.w3.org/1999/xhtml"));
        test3.setContentEncoding( new String("http://www.w3.org/TR/REC-xml#dt-wellformed"));
        test3.setContentEncoding(new String("Literal"));
        //test3.setContentValueNamespace(new String("http://www.w3.org/1999/xhtml"));
        test3.setContentValue(new String("<em>This is <strong>very</strong></em> <strong>cool</strong>."));
        //assertEquals( test2, test3 );
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
}
