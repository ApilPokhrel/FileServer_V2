



$("#file_upload").on("submit",function (event) {
    event.preventDefault();
    console.log("insode upload ");
    upload();
});
let upload = ()=>{
   let bucketName = $('#bucket_name_title').text();
    console.log("inside upload");
    $.ajax({
        url: `http://localhost/file/?bucketName=${bucketName}`,
        type: "POST",
        headers: { access_token: $.cookie("access_token") },
        data: new FormData($("#file_upload")[0]),
        enctype: "multipart/form-data",
        processData: false,
        contentType: false,
        cache: false,
        success: function(d) {
            alert("File succesfully uploaded", d);

        },
        error: function(e) {
            // Handle upload error
            console.log(e)
            alert(e.responseText);
        }
    });
}