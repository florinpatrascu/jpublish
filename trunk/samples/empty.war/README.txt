This folder contains an empty JPublish web application.


Few hints for the impatient :)

1. To modify the home page layout, edit the file:
	empty.war/templates/basic.html

2. To modify the home page contents, edit the file:
	empty.war/content/index.html

3. The simple CSS file is situated here: empty.war/public/styles/simple.css

4. To add and use a scripting action:
 - create a new Beanshell (bsh) or a Rhino(js) Action script in the empty.war/actions
   folder
 - use the new Action in the page you need by declaring the new Action in the page's
   specific xml file:
      ...
      <page-action name="the newly created action"/>
      ...


Good luck!