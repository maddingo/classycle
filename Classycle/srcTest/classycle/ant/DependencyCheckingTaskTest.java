/*
 * Created on 23.11.2004
 */
package classycle.ant;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import org.apache.tools.ant.BuildException;

/**
 * @author  Franz-Josef Elmer
 */
public class DependencyCheckingTaskTest extends ClassycleTaskTestCase
{
  public DependencyCheckingTaskTest(String arg0)
  {
    super(arg0);
  }

  protected void setUp() throws Exception
  {
    createTempDir();
    configureProject("dependencyCheckingTaskTestBuild.xml");
  }
  
  private void checkNumberOfOutputLines(int number) throws IOException
  {
    checkNumberOfLines(new StringReader(getOutput()), number);
  }
  
  private void checkLine(String expectedLine, int lineNumber) throws IOException
  {
    checkLine(new StringReader(getOutput()), expectedLine, lineNumber);
  }
  
  public void testEmbeddedDefinitions() throws Exception
  {
    executeTarget("testEmbeddedDefinitions");
    checkNumberOfOutputLines(14);
    checkLine("  <unexpected-dependencies statement='check [A] independentOf [non-A]'/>", 3);
    checkLine("  <unexpected-dependencies statement='check [non-A] independentOf [A]'>", 4);
  }
  
  public void testEmbeddedDefinitionsFromJar() throws Exception
  {
    executeTarget("testEmbeddedDefinitionsFromJar");
    checkNumberOfOutputLines(14);
    checkLine("  <unexpected-dependencies statement='check [A] independentOf [non-A]'/>", 3);
    checkLine("  <unexpected-dependencies statement='check [non-A] independentOf [A]'>", 4);
  }
  
  public void testEmbeddedDefinitionsFailureOn() throws Exception
  {
    try
    {
      executeTarget("testEmbeddedDefinitionsFailureOn");
      fail("BuildException expected");
    } catch (BuildException e)
    {
      checkNumberOfOutputLines(10);
      checkLine("check [A] independentOf [non-A]\tOK", 2);
      checkLine("check [non-A] independentOf [A]", 3);
      checkLine("  Unexpected dependencies found:", 4);
    }
  }
  
  public void testCheckCyclesMergedInnerClassesFailureOn() throws Exception
  {
    executeTarget("testCheckCyclesMergedInnerClassesFailureOn");
    assertEquals("<?xml version='1.0' encoding='UTF-8'?>\n" 
            + "<dependency-checking-results>\n"
            + "  <cycles statement='check absenceOfClassCycles &gt; 1 "
            +          "in example.*' vertex-type='class'/>\n" 
            + "</dependency-checking-results>\n",
            readFile("dependency-checking-result.xml"));
  }
  
  public void testCheckCyclesFailureOn() throws Exception
  {
    try
    {
      executeTarget("testCheckCyclesFailureOn");
      fail("BuildException expected");
    } catch (BuildException e)
    {
      checkNumberOfOutputLines(4);
      checkLine("check absenceOfClassCycles > 1 in example.*", 1);
      checkLine("  example.B and inner classes contains 2 classes:", 2);
      checkLine("    example.B", 3);
      checkLine("    example.B$M", 4);
    }
  }
  
  public void testExcluding() throws Exception
  {
    executeTarget("testExcluding");
    checkNumberOfOutputLines(8);
  }
  
  public void testResetGraphAfterCheck() throws Exception
  {
    executeTarget("testResetGraphAfterCheck");
    checkNumberOfOutputLines(8);
    checkLine("check [A-not-p] independentOf *h*", 1);
    checkLine("check [A-not-p] independentOf *S*", 5);
    checkLine("    -> java.lang.String", 8);
  }
  
  public void testDependentOnlyOn() throws Exception
  {
    executeTarget("testDependentOnlyOn");
    assertEquals("check example.B* dependentOnlyOn java.lang.* example.A*\n"
            + "  Unexpected dependencies found:\n" 
            + "  example.B$M\n" 
            + "    -> example.p.A\n"
            + "  example.BofA\n" 
            + "    -> example.p.A\n"
            + "check [set] dependentOnlyOn java.lang.*\n" 
            + "  Unexpected dependencies found:\n"
            + "  example.B\n" 
            + "    -> example.A\n" 
            + "  example.B$M\n"
            + "    -> example.A\n" 
            + "  example.p.A\n" 
            + "    -> example.A\n"
            + "check example.B* dependentOnlyOn *A\n"
            + "  Unexpected dependencies found:\n" 
            + "  example.B$M\n"
            + "    -> java.lang.Object\n" 
            + "  example.BofA\n" 
            + "    -> java.lang.Object\n"
            + "    -> java.lang.Class\n" 
            + "    -> java.lang.Thread\n"
            , getOutput());
  }
  
  public void testReflection() throws Exception
  {
    executeTarget("testReflection");
    checkNumberOfOutputLines(6);
    checkLine("check [A-not-p] independentOf *h*", 1);
    assertTrue(getOutput().indexOf("-> hello") > 0);
  }
  
  public void testReflectionWithRestriction() throws Exception
  {
    executeTarget("testReflectionWithRestriction");
    checkNumberOfOutputLines(4);
    checkLine("check [A-not-p] independentOf *h*", 1);
    assertTrue(getOutput().indexOf("-> java.lang.Thread") > 0);
  }
  
  public void testFile() throws Exception
  {
    Writer writer = new FileWriter(TMP_DIR + "/test.ddf");
    writer.write("show allResults\n"
                 + "[A] = *A*\n"
                 + "[non-A] = example.* excluding [A]\n"
                 + "check [A] independentOf [non-A]");
    writer.close();
    executeTarget("testFile");
    checkNumberOfOutputLines(2);
    checkLine("check [A] independentOf [non-A]\tOK", 2);
  }
  
  public void testNoClasses() throws Exception
  {
    try
    {
      executeTarget("testNoClasses");
      fail("BuildException expected");
    } catch (BuildException e)
    {
      checkNumberOfOutputLines(0);
    }
  }

  public void testEmpty() throws Exception
  {
    try
    {
      executeTarget("testEmpty");
      fail("BuildException expected");
    } catch (BuildException e)
    {
      checkNumberOfOutputLines(0);
      assertEquals("Empty dependency definition.", e.getMessage());
    }
  }
}
