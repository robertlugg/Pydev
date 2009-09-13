/* 
 * Copyright (C) 2007  Reto Schuettel, Robin Stocker
 *
 * IFS Institute for Software, HSR Rapperswil, Switzerland
 * 
 */

package org.python.pydev.refactoring.tests.coderefactoring.extractlocal;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.python.pydev.core.REF;
import org.python.pydev.refactoring.coderefactoring.extractlocal.ExtractLocalRefactoring;
import org.python.pydev.refactoring.coderefactoring.extractlocal.ExtractLocalRequestProcessor;
import org.python.pydev.refactoring.core.base.RefactoringInfo;
import org.python.pydev.refactoring.tests.core.AbstractIOTestCase;

public class ExtractLocalTestCase extends AbstractIOTestCase {

	public ExtractLocalTestCase(String name) {
		super(name);
	}

	@Override
	public void runTest() throws Throwable {
	    REF.IN_TESTS = true;
	    
	    IDocument document = new Document(data.source);
	    ITextSelection selection = new TextSelection(document, data.sourceSelection.getOffset(), data.sourceSelection.getLength());
	    RefactoringInfo info = new RefactoringInfo(document, selection);
	    ExtractLocalRefactoring refactoring = new ExtractLocalRefactoring(info);
	    
	    ExtractLocalRequestProcessor requestProcessor = refactoring.getRequestProcessor();
	    requestProcessor.setVariableName("extracted_variable");
	    
	    NullProgressMonitor monitor = new NullProgressMonitor();
	    RefactoringStatus result = refactoring.checkAllConditions(monitor);
	    
	    assertTrue("Refactoring is not ok: " + result.getMessageMatchingSeverity(RefactoringStatus.WARNING), result.isOK());
	    
	    Change change = refactoring.createChange(monitor);
	    change.perform(monitor);
	    
	    assertEquals(data.result, document.get());
	    
		REF.IN_TESTS = false;
	}
}