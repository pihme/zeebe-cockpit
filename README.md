# zeebe-cockpit
web frontend to monitor a running zeebe cluster

## Gradle Tasks
Whenever you want to produce a minified "production" version of your code pass in `-Pproduction=true` or `-Pprod=true` to your build command.
### Resource Processing
* generatePotFile - Generates a `src/frontendMain/resources/i18n/messages.pot` translation template file.
### Compiling
* compileKotlinFrontend - Compiles frontend sources.
* compileKotlinBackend - Compiles backend sources.
### Running
* frontendRun - Starts a webpack dev server on port 3000
* backendRun - Starts a dev server on port 8080
### Packaging
* frontendBrowserWebpack - Bundles the compiled js files into `build/distributions`
* frontendJar - Packages a standalone "web" frontend jar with all required files into `build/libs/*.jar`
* backendJar - Packages a backend jar with compiled source files into `build/libs/*.jar`
* jar - Packages a "fat" jar with all backend sources and dependencies while also embedding frontend resources into `build/libs/*.jar

## Branches
* master - stable version (expect this branch to remein empty for a time being)
* volatile/* - experimental code to try out ideas, features, technologies
