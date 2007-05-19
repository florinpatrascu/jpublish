This is a JPublish component which displays a Tag Cloud.

This work was inspired by Simon Brown's amazing product: http://pebble.sourceforge.net

Th tagCloud code here was redesigned as an embed-able JPublish component and in the spirit of IoC,
there is a very easy way to control the data definition ad well as the tagCloud view layout, all these
by simply tuning your CSS definitions and by using a simple Velocity/Freemarker view with support from
a simple Java script or pure Java Action.

Easy to use, efficient and nice. But I am sure you are already familiar with these words if you are a
JPublish user ;)


Usage:
- add the supplied TagCloudComponent.jar library to your WEB-INF/lib folder.
- add the following component definition to jpublish.xml or modify your component manager
  definition if you already have other components installed

     <component-manager>
         <components>
             <component id="tagCloud" classname="ca.flop.jpublish.tags.TagCloudComponent">
                 <repository name="fs_repository" view="tagcloud.html" execute="TestCloudAction.bsh"/>
                 <tag-separator separator=","/>
                 <normalizer>10</normalizer>
             </component>
         </components>
     </component-manager>

- create a view to be used for rendering the TAGs and an Action (Java or script) to populate the
tagCloud with data. We provide the TestCloudAction.bsh action to ilustrate how you should provide the
data to the tagCloud component and we also provide a demo view to start with.


Where ever you'll need to display the tagCloud, you can simply invoke the component and display the
tagCloud:

    $components.tagCloud

To control the tagCloud layout please see the tagcloud.html view and the styles/cloud.css.

Easy, isn't it?

To make it even easier, after you build the jpublish framework, go to the components/tagCloud folder and run ANT. In the components/tagCloud/dist/ you'll find a ready packed web application demonstrating the tagcloud; is called jptags.war

Enjoy the TagCloud component,
-florin
