package com.cgi.example.petstore;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@AnalyzeClasses(
    packages = "com.cgi.example.petstore",
    importOptions = {
      ImportOption.OnlyIncludeTests.class,
      ImportOption.DoNotIncludeJars.class,
      ImportOption.DoNotIncludeArchives.class
    })
public class TestArchitectureTest {

  @ArchTest
  public static final ArchRule ARCHITECTURE_TEST_1 =
      classes()
          .that()
          .areNotAnnotatedWith(Disabled.class)
          .should()
          .haveSimpleNameEndingWith("Test");

  @ArchTest
  public static final ArchRule ARCHITECTURE_TEST_2 =
      classes()
          .that()
          .areAnnotatedWith(Disabled.class)
          .should()
          .haveSimpleNameNotEndingWith("Test");

  @ArchTest
  public static final ArchRule ARCHITECTURE_TEST_3 =
      classes()
          .that()
          .resideInAPackage("com.cgi.example.petstore.utils..")
          .and()
          .doNotHaveSimpleName("ProcessManagementTest")
          .should()
          .beAnnotatedWith(Disabled.class);

  @ArchTest
  public static final ArchRule ARCHITECTURE_TEST_4 =
      classes()
          .that()
          .resideInAPackage("com.cgi.example.petstore.utils..")
          .and()
          .doNotHaveSimpleName("ProcessManagementTest")
          .should()
          .onlyHaveDependentClassesThat()
          .areNotAnnotatedWith(Test.class);
}
