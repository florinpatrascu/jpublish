<h1>$page.title$</h1>
This is a very simple <strong>JPublish</strong> application using the StringTemplate framework for page rendering.<br/>
To learn more about <strong>StringTemplate</strong> please follow the <a href="http://www.stringtemplate.org/">link</a>.

<p>
Page property declared in the pages/index.xml: <strong>$pageInstanceProperties.foo.value$</strong><br/>
Page property added to the context by the HomePageAction.bsh :
<strong>$homePage$</strong><br/>
And some UNICODE ('terms and conditions' in German):
<b><strong>$pageInstanceProperties.someUNICODE.value$</strong></b><br/><br/><br/>

Files in 'fs_repository':<br/>
$fs_repository.keys:{k| $k$};separator="<br>";$
</p>
