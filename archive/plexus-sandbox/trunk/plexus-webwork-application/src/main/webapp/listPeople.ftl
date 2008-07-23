<html>
<head>
    <title>All People</title>
</head>

<body>
<table>
    <tr>
        <th>ID</th>
        <th>Name</th>
    </tr>
<#list people as person>
    <tr>
        <td>${person.id}</td>
        <td>${person.name}</td>
    </tr>
</#list>
</table>
</body>
</html>