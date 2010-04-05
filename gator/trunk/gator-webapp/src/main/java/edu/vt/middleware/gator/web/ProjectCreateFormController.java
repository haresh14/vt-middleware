/*
  $Id: $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.gator.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import edu.vt.middleware.gator.AppenderPolicy;
import edu.vt.middleware.gator.ProjectConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles creation of new projects.
 *
 * @author Middleware
 * @version $Revision: $
 *
 */
@Controller
@RequestMapping("/secure")
public class ProjectCreateFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "projectEdit";
  
  /** Appender policy to be used for newly-created projects */
  @Autowired
  private AppenderPolicy appenderPolicy;


  @RequestMapping(value = "/project/add.html", method = RequestMethod.GET)
  public String getNewProject(final Model model)
  {
    final ProjectConfig project = new ProjectConfig();
    if (appenderPolicy != null) {
      project.setAppenderPolicy(appenderPolicy);
    }
    model.addAttribute("project", project);
    return VIEW_NAME;
  }


  @RequestMapping(value = "/project/add.html", method = RequestMethod.POST)
  @Transactional(propagation = Propagation.REQUIRED)
  public String saveProject(
      @Valid @ModelAttribute("project") final ProjectConfig project,
      final BindingResult result,
      final HttpServletRequest request)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    // Add all permissions to new project for current user principal
    project.addPermission(
      ControllerHelper.createAllPermissions(
        request.getUserPrincipal().getName()));
    logger.debug("Saving " + project);
    configManager.save(project);
    return String.format(
        "redirect:/secure/project/%s/edit.html", project.getName());
  }
}
