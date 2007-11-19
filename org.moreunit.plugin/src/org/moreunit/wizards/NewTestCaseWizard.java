package org.moreunit.wizards;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.moreunit.elements.JavaProjectFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;

public class NewTestCaseWizard extends NewClassyWizard {

	private MoreUnitNewTestCaseWizardPageOne	pageOne;
	private NewTestCaseWizardPageTwo	pageTwo;

	private IJavaProject project;
	private Preferences preferences;

	public NewTestCaseWizard(final IType element) {
		super(element);

		this.project = element.getJavaProject();
		this.preferences = Preferences.newInstance(this.project);
	}

	@Override
	public void addPages() {
		this.pageTwo = new NewTestCaseWizardPageTwo();
		this.pageOne = new MoreUnitNewTestCaseWizardPageOne(this.pageTwo, this.preferences);
		this.pageOne.setWizard(this);
		this.pageTwo.setWizard(this);
		this.pageOne.init(new StructuredSelection(getType()));
		initialisePackageFragment();
		addPage(this.pageOne);
		addPage(this.pageTwo);
	}
	
	


	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		// Eclipse 3.1.x  does not support junit 4
		try {
		this.pageOne.setJUnit4(this.preferences.shouldUseJunit4Type(), true);
		} catch (NoSuchMethodError error) {
		}
		this.pageOne.setSuperClass(this.preferences.getTestSuperClass(), true);
		//set default and focus
		String classUnderTest= pageOne.getClassUnderTestText();
		if (classUnderTest.length() > 0) {
			final String[] suffixes = preferences.getSuffixes();
			final String suffix;
			if (suffixes.length > 0) {
				suffix = suffixes[0];
			} else {
				suffix = PreferenceConstants.DEFAULT_SUFFIX;
			}
			this.pageOne.setTypeName(Signature.getSimpleName(classUnderTest) + suffix, true);
		}
		this.pageOne.setPackageFragmentRoot(getPackageFragmentRootFromSettings(), true);	
	}

	@Override
	protected IPackageFragmentRoot getPackageFragmentRootFromSettings() {
		return (new JavaProjectFacade(this.project)).getJUnitSourceFolder();
	}

	private void initialisePackageFragment() {
		String testPackagePrefix = this.preferences.getTestPackagePrefix();
		String testPackageSuffix = this.preferences.getTestPackageSuffix();

		boolean hasPrefix = (testPackagePrefix != null) &&  (testPackagePrefix.length() > 0);
		boolean hasSuffix = (testPackageSuffix != null) &&  (testPackageSuffix.length() > 0);

		if (!hasPrefix && !hasSuffix) {
			return;
		}

		String fragment = null;
		if(hasPrefix) {
			fragment = getPackageFragmentNameWithPrefix();
		} else {
			fragment = getPackageFragmentNameWithSuffix();
		}

		try {
			IPackageFragment packageFragment = createPackageFragment(fragment);
			this.pageOne.setPackageFragment(packageFragment, true);
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleWarnLog("Unable to create package fragment root");
		}
	}

	private String getPackageFragmentNameWithPrefix() {
		return this.preferences.getTestPackagePrefix() + "." + this.pageOne.getPackageFragment().getElementName();
	}

	private String getPackageFragmentNameWithSuffix() {
		//return BaseTools.addPackageFragmentSuffixToElementName(pageOne.getPackageFragment().getElementName(), Preferences.instance().getTestPackageSuffix());
		return this.pageOne.getPackageFragment().getElementName() + "." + this.preferences.getTestPackageSuffix();
	}

	@Override
	public IType createClass() throws CoreException, InterruptedException {
		this.pageOne.createType(new NullProgressMonitor());
		return this.pageOne.getCreatedType();
	}

	@Override
	protected IPackageFragmentRoot getPackageFragmentRoot() {
		return this.pageOne.getPackageFragmentRoot();
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.7  2007/09/02 19:25:42  gianasista
// TestNG support
//
// Revision 1.6  2007/08/24 18:31:59  gianasista
// TestNG support
//
// Revision 1.5  2007/08/24 18:30:46  gianasista
// TestNG support
//
// Revision 1.4  2007/08/11 17:09:05  gianasista
// Applied patch for super class
//
// Revision 1.3  2006/11/25 15:01:21  gianasista
// organize import
//
// Revision 1.2  2006/10/08 17:28:29  gianasista
// Suffix preference
//
// Revision 1.1.1.1  2006/08/13 14:31:16  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:22:29  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.3  2006/06/11 20:09:04  gianasista
// added package prefix preference and support for eclipse 3.1.x
//
// Revision 1.2  2006/05/31 19:40:23  gianasista
// Preferences are used for initialization of the wizard
//
// Revision 1.1  2006/05/12 22:33:41  channingwalton
// added class creation wizards if type to jump to does not exist
//