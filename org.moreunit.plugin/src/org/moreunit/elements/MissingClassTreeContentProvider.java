package org.moreunit.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.ui.MissingClassPage;
import org.moreunit.ui.MissingTestsViewPart;
import org.moreunit.util.PluginTools;

public class MissingClassTreeContentProvider implements ITreeContentProvider
{
    //private List<IType> classesNotUnderTest = new ArrayList<IType>();

    public Object[] getChildren(Object arg0)
    {
        return null;
    }

    public Object getParent(Object arg0)
    {
        return null;
    }

    public boolean hasChildren(Object arg0)
    {
        return false;
    }

    public Object[] getElements(Object inputElement)
    {
        List<Object> elements = new ArrayList<Object>();

        if(inputElement instanceof MissingTestsViewPart)
        {
            IJavaProject javaProject = ((MissingTestsViewPart) inputElement).getSelectedJavaProject();
            if(javaProject != null)
            {
                List<IPackageFragmentRoot> allSourceFolderFromProject = PluginTools.getAllSourceFolderFromProject(javaProject);
                for (IPackageFragmentRoot sourceFolder : allSourceFolderFromProject)
                {
                    try
                    {
                        IJavaElement[] children = sourceFolder.getChildren();
                        for (IJavaElement javaPackage : children)
                        {
                            ICompilationUnit[] compilationUnits = ((IPackageFragment) javaPackage).getCompilationUnits();
                            for (ICompilationUnit compilationUnit : compilationUnits)
                            {
                                if(TestCaseTypeFacade.isTestCase(compilationUnit.findPrimaryType()))
                                    elements.add(compilationUnit);

                            }
                        }
                    }
                    catch (JavaModelException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            // javaProject.get
        }
        /*
         * if (inputElement instanceof MissingClassPage){ MissingClassPage page
         * = (MissingClassPage) inputElement; classesNotUnderTest.clear();
         * Set<IType> result = page.getClassesNotUnderTest(); if (result !=
         * null) { classesNotUnderTest.addAll(result); } } return
         * classesNotUnderTest.toArray();
         */

        return elements.toArray();
    }

    public void dispose()
    {

    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
    {

    }

}
