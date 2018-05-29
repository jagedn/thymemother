package com.thymemother.controller

import com.thymemother.controller.model.User
import com.thymemother.dsl.RootSpec
import com.thymemother.dsl.MapSpec
import com.thymemother.dsl.ModelSpec
import com.thymemother.dsl.HelperExtension
import com.thymemother.model.Author
import com.thymemother.model.Book
import org.codehaus.groovy.control.CompilerConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import io.beanmother.core.*

@Controller
public class Thymeleaf3Controller {

    Logger log = LoggerFactory.getLogger(Thymeleaf3Controller.class);

    @RequestMapping(value="/{template}", method=RequestMethod.GET)
    public String selectAdorable(@PathVariable("template") String template, Model m) {
        // Overwrite source of directory fixture by system properties...
        def ObjectMother objectMother = ObjectMother.getInstance();
        String fixturePath = System.properties['fixtureDir']
        objectMother.addFixtureLocation(fixturePath)

        // Get the source code of external dsl, by default or by properties
        // this it's the same of the template, but with another extension
        String code
        String templatePath = System.properties['spring.thymeleaf.prefix']
        if (templatePath==null) {
            code = new ClassPathResource("templates/"+template+".groovy").getFile().text
        } else {
            String groovyDsl = templatePath+"/"+template+".groovy"
            if (groovyDsl.startsWith("file://")) {
                groovyDsl = groovyDsl.substring(7)
            }
            code = new File(groovyDsl).text
        }

        // Pass the variables to the DSL
        Binding binding = new Binding();
        binding.setProperty("m", m);
        binding.setProperty("objectMother", objectMother);

        // Execute DSL
        Script script = new GroovyShell(binding).parse(code)
        script.run()

        return template;
    }

}
