<%@ taglib prefix="ww" uri="webwork" %>
<html>
  <head><title>File Upload</title></head>
  <body>

  <ww:form action="'fileUpload'" enctype="'multipart/form-data'" method="'post'" >
    <ww:file label="'File'" name="'file'"/>
    <ww:file label="'File(s)'" name="'files'"/>
    <ww:file label="'File(s)'" name="'files'"/>
    <ww:file label="'File(s)'" name="'files'"/>
    <ww:submit value="'Upload'"/>
  </ww:form>

  </body>
</html>