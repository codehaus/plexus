<%@ taglib uri="webwork" prefix="ww" %>

<html>
    <head>
        <title>JavaScript Validation Input</title>
        <link rel="stylesheet" href="styles.css">
        <ww:component template="'validation'"/>
    </head>

    <body>
        <ww:form name="'javascriptValidation'" action="'javascriptValidation'" validate="true" >
            <ww:textfield label="'Required String'" name="'requiredString'" />
            <ww:textfield label="'Some Int'" name="'intRange'" />
            <ww:select label="'Email (select)'" name="'email'" list="{'Select', 'foo@bar.com', 'baz@biz.com'}"/>
            <ww:textfield label="'URL'" name="'url'" />
            <ww:textfield label="'Date'" name="'date'" />
            <tr><td colspan="2"><hr/></td></tr>
            <ww:textfield label="'Bean Text'" name="'bean.text'" />
            <ww:textfield label="'Bean Date'" name="'bean.date'" />
            <ww:textfield label="'Bean Number'" name="'bean.number'" />
            <ww:submit value="'Submit'"/>
        </ww:form>
    </body>
</html>