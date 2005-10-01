/*
 * License: Common Public License v1.0
 * Created on Sep 13, 2005
 * 
 * @author Fabio Zadrozny
 */
package com.python.pydev.analysis.additionalinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.python.pydev.plugin.PydevPlugin;
import org.python.pydev.plugin.nature.PythonNature;


public class AdditionalProjectInterpreterInfo extends AbstractAdditionalDependencyInfo{

    private IProject project;
    /**
     * holds nature info (project name points to info)
     */
    private static Map<String, AbstractAdditionalDependencyInfo> additionalNatureInfo = new HashMap<String, AbstractAdditionalDependencyInfo>();

    public AdditionalProjectInterpreterInfo(IProject project) {
        this.project = project;
    }

    @Override
    protected String getPersistingLocation() {
        String name = project.getName();
        if(name == null || name.trim().length() == 0){
            throw new RuntimeException("The name of the project is not valid: "+project);
        }
        return getPersistingFolder()+name+".pydevinfo";
    }

    @Override
    protected void setAsDefaultInfo() {
        AdditionalProjectInterpreterInfo.setAdditionalInfoForProject(project, this);
    }

    public static void saveAdditionalInfoForProject(IProject project) {
        AbstractAdditionalInterpreterInfo info = getAdditionalInfoForProject(project);
        info.save();
    }

    /**
     * @param nature the nature we want to get info on
     * @return all the additional info that is bounded with some nature (including related projects)
     */
    public static List<AbstractAdditionalInterpreterInfo> getAdditionalInfo(PythonNature nature) {
        List<AbstractAdditionalInterpreterInfo> ret = new ArrayList<AbstractAdditionalInterpreterInfo>();
        IProject project = nature.getProject();
        
        //get for the system info
        AbstractAdditionalInterpreterInfo systemInfo = AdditionalSystemInterpreterInfo.getAdditionalSystemInfo(PydevPlugin.getInterpreterManager(nature));
        ret.add(systemInfo);
    
        //get for the current project
        AbstractAdditionalInterpreterInfo additionalInfoForProject = getAdditionalInfoForProject(project);
        if(additionalInfoForProject != null){
            ret.add(additionalInfoForProject);
        }
        
        try {
            //get for the referenced projects
            IProject[] referencedProjects = project.getReferencedProjects();
            for (IProject refProject : referencedProjects) {
                
                additionalInfoForProject = getAdditionalInfoForProject(refProject);
                if(additionalInfoForProject != null){
                    ret.add(additionalInfoForProject);
                }
            }
            
            
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * @param project the project we want to get info on
     * @return the additional info for a given project (gotten from the cache with its name)
     */
    public static AbstractAdditionalDependencyInfo getAdditionalInfoForProject(IProject project) {
        String name = project.getName();
        AbstractAdditionalDependencyInfo info = additionalNatureInfo.get(name);
        if(info == null){
            info = new AdditionalProjectInterpreterInfo(project);
            additionalNatureInfo.put(name, info);
        }
        return info;
    }

    /**
     * sets the additional info (overrides if already set)
     * @param project the project we want to set info on
     * @param info the info to set
     */
    public static void setAdditionalInfoForProject(IProject project, AbstractAdditionalDependencyInfo info) {
        additionalNatureInfo.put(project.getName(), info);
    }

    public static boolean loadAdditionalInfoForProject(IProject project) {
        AbstractAdditionalDependencyInfo info = getAdditionalInfoForProject(project);
        return info.load();
    }

}
