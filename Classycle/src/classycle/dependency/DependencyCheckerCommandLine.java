/*
 * Copyright (c) 2003-2004, Franz-Josef Elmer, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package classycle.dependency;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import classycle.CommandLine;

/**
 * @author  Franz-Josef Elmer
 */
public class DependencyCheckerCommandLine extends CommandLine
{
  private static final String DEPENDENCIES = "-dependencies=";
  
  private String _dependencyDefinition;
  
  public DependencyCheckerCommandLine(String[] args)
  {
    super(args);
  }

  protected void handleOption(String argument)
  {
    if (argument.startsWith(DEPENDENCIES)) 
    {
      handleDependenciesOption(argument.substring(DEPENDENCIES.length()));
    } else
    {
      super.handleOption(argument);
    }
  }

  /** Returns the usage of correct command line arguments and options. */
  public String getUsage() 
  {
    return "[" + DEPENDENCIES + "<description>|" 
               + DEPENDENCIES + "@<description file>] " + super.getUsage();
  }
  
  public String getDependencyDefinition()
  {
    return _dependencyDefinition;
  }
  
  private void handleDependenciesOption(String option)
  {
    if (option.startsWith("@"))
    {
      try
      {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader 
            = new BufferedReader(new FileReader(option.substring(1)));
        String line;
        while ((line = reader.readLine()) != null)
        {
          buffer.append(line).append('\n');
        }
        option = new String(buffer);
      } catch (IOException e)
      {
        System.err.println("Error in reading dependencies description file: " 
                           + e.toString());
        option = "";
      }
    }
    _dependencyDefinition = option;
    if (_dependencyDefinition.length() == 0) 
    {
      _valid = false;
    }
  }
}