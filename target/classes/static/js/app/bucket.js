

$(document).on("submit", "#bucket_create", function (event) {
    event.preventDefault();
    createBucket("bucket_create");
});

$(document).on("submit", "#bucket_update", function (event) {
    event.preventDefault();
    updateBucket("bucket_update");
})






function createBucket(form){

    let owners =$('[name=owners]').val();
    let allowed_file_type = [];
    $(`input:checkbox[name=allowed_file_type]:checked`).each(function(){
        allowed_file_type.push($(this).val());
    });

    let allowed_methods = [];
    $(`input:checkbox[name=allowed_methods]:checked`).each(function(){
        allowed_methods.push($(this).val());
    });

    let data = {
        name : $('[name=name]').val(),
        owners: owners.toString(),
        threshold: $('[name=threshold]').val(),
        allowed_file_type: allowed_file_type.toString(),
        allowed_methods: allowed_methods.toString(),
    }
    $.ajax({
        url: `/api/v1/bucket/create`,
        headers: { access_token: $.cookie("access_token") },
        method: "post",
        data,
    })
        .done(d => {
if(d){
    swal({
        title: "Bucket Create Failed",
        html: true,
        text: "Successfully created Bucket",
        icon: "success",
    });
}        })
        .fail(e => {

            let error;
            if(e.responseText) error = e.responseText
            if(e.responseJSON) error = e.responseJSON

            if(typeof error == "function") {
                error = error.map(e => {
                    return `${e}`;
                }).join("<br><br>");
            }
            swal({
                title: "Bucket Create Failed",
                html: true,
                text: error,
                icon: "error",
            });
        });
}

 let setBucketEdit = (bucket)=>{
    bucket = $('#bucket_name_title').text();
    if(bucket) {
        $.ajax({
            url: `/api/v1/bucket/${bucket}`,
            headers: {access_token: $.cookie("access_token")},
            method: "GET"
        }).done(d => {
            if(d.files) filesDataTable(d)

            for (var o of d.owners) {
                if(o.trim()) {
                    $("#add_owner_edit").append(`<option value="${o}" selected="selected" >${o}</option>`);
                }
            }
            $("#add_owner_edit").select2({
                tags: true,
                tokenSeparators: [',', ' ']
            })
            for (var m of d.allowed_methods) {
                if (m == "get") {
                    $('#allowed_methods #get').prop('checked', true);
                }
                if (m == "post") {
                    $('#allowed_methods #post').prop('checked', true);
                }
                if (m == "delete") {
                    $('#allowed_methods #delete').prop('checked', true);
                }
            }
            for (var t of d.allowed_file_type) {
                if (t == "image") {
                    $('#allowed_file_type #image').prop('checked', true);
                }
                if (t == "video") {
                    $('#allowed_file_type #video').prop('checked', true);
                }
                if (t == "document") {
                    $('#allowed_file_type #document').prop('checked', true);
                }
            }

            $(`#exampleSelect1 #${d.threshold}`).attr('selected', 'selected');
            thresholdChart(d);
            setTotalRequestBucket(d);
        }).fail(e => {
            let error;
            if (e.responseText) error = e.responseText;
            if (e.responseJSON) error = e.responseJSON;

            swal({
                title: "Bucket Load Failed",
                html: true,
                text: error,
                icon: "error",
            });
        })
    }
 }


function updateBucket(form){

    let owners =$('[name=owners]').val();
    let allowed_file_type = [];
    $(`input:checkbox[name=allowed_file_type]:checked`).each(function(){
        allowed_file_type.push($(this).val());
    });

    let allowed_methods = [];
    $(`input:checkbox[name=allowed_methods]:checked`).each(function(){
        allowed_methods.push($(this).val());
    });

    let data = {
        name : $('[name=name]').val(),
        owners: owners.toString(),
        threshold: $('[name=threshold]').val(),
        allowed_file_type: allowed_file_type.toString(),
        allowed_methods: allowed_methods.toString(),
    }
    $.ajax({
        url: `/api/v1/bucket/${data.name}`,
        headers: { access_token: $.cookie("access_token") },
        method: "PATCH",
        data,
    })
        .done(d => {
            console.log("inside patch ", d)
            if(d){
                swal({
                    title: "Bucket Update",
                    html: true,
                    text: "Successfully updated Bucket",
                    icon: "success",
                });
            }        })
        .fail(e => {

            let error;
            if(e.responseText) error = e.responseText
            if(e.responseJSON) error = e.responseJSON

            if(typeof error == "function") {
                error = error.map(e => {
                    return `${e}`;
                }).join("<br><br>");
            }
            swal({
                title: "Bucket Update Failed",
                html: true,
                text: error,
                icon: "error",
            });
        });
}

 setBucketEdit();


let deleteBucket = ()=>{
    if(confirm("Are you sure to delete")) {
        $.ajax({
            url: `/api/v1/bucket/${$('[name=name]').val()}`,
            headers: {access_token: $.cookie("access_token")},
            method: "delete",

        })
            .done(d => {
                if (d) {
                    swal({
                        title: "Bucket Delete",
                        html: true,
                        text: "Successfully updated Bucket",
                        icon: "success",
                    },
                        function(){
                            window.location.href = "?"
                        });
                }
            })
            .fail(e => {

                let error;
                if (e.responseText) error = e.responseText
                if (e.responseJSON) error = e.responseJSON
                swal({
                    title: "Bucket Delete Failed",
                    html: true,
                    text: error,
                    icon: "error",
                });
            });
    }
}


let filesDataTable = (bucket)=>{
    let d = [];
    // $('#bucketTable').html("");
    for(var itm of bucket.files){
        if(itm.status == "true"){
            d.push(itm);
        }
    }
    var ind = 1;
    $("#bucketTable").DataTable({
        pageLength: 10,
        processing: true,
        responsive: true,
        filter: true,
        sort: true,
        bFilter: true,
        searching: true,
        paging: true,
        info: true,
        // dom: "<'row'<'col-sm-12'tr>>" + "<'row'<'col-sm-4'i><'col-sm-8'<'float-right p-2'p>>>",
       data: d,

        columns: [
            {
                data: {},
                render: function(data, type, full) {
                       return ind++;
                }
            },
            {
                data: "name",

            },
            {
                data: "type",

            },
            {
                data: "uploadedAt",
                render: function (time) {
                   return moment(time).format('YYYY/MM/D hh:mm:ss SSS');
                }

            },
            {
                data: "size",
                render: function (size) {
                    return (size * 0.000001).toFixed(2) + " mb";
                }
            },
            {
                data: "status",
                render: function(status) {
                    if (status) {
                        return `${status}`;
                    } else {
                        return `---`;
                    }
                }
            },

            {
                data: {id: "_id", name: "name"},
                render: function(data) {
                    return `<button class="btn btn-white btn-sm" onclick="deleteFile('${bucket.id}', '${data.id}')"><i class="fa fa-trash"></i></button>
                <a href="/file/${bucket.name}/${data.name}" class="btn btn-white btn-sm" ><i class="fa fa-eye"></i></a>
                <a href="/file/${bucket.name}/${data.name}?download=yes" class="btn btn-white btn-sm" ><i class="fa fa-download"></i></a>`;
                }
            }
        ]
    });
}

$("#bucketListTable").DataTable({
    pageLength: 25,
    processing: true,
    responsive: true,
    filter: true,
    sort: false,
    serverSide: true,
    searchDelay: 500,
    dom: "<'row'<'col-sm-12'tr>>" + "<'row'<'col-sm-4'i><'col-sm-8'<'float-right p-2'p>>>",
    ajax: {
        url:"/api/v1/bucket/list",
        method: "GET",
        headers: { access_token: $.cookie("access_token") },
        dataFilter: data => {
            let json = JSON.parse(data);
            json.recordsTotal = json.total;
            json.recordsFiltered = json.total;
            return JSON.stringify(json); // return JSON string
        },
        data: function(d) {
            return $.extend(
                {},
                {
                    skip: d.start,
                    limit: d.length,
                    q: d.search.value
                }
            );
        },
        dataSrc:"",

    },

    columns: [
        {
            data: {},
            render: function(data, type, full) {
                return index++;
            }
        },
        {
            data: "name",

        },
        {
            data: "requests",
            render: function (requests) {
                return (requests) ? requests.length : 0;
            }

        },
        {
            data: "files",
            render: function (files) {
                return (files)? files.length : 0;
            }

        },

        {
            data: null,
            render: function (data) {
                if(!data.status || (data.status == "active")){
                    return `
                        <input type="checkbox" class="checkbox" id="${data.id}" checked name="status" value="active" onclick="changeBucketStatus('${data.name}', '${data.id}')">
                     `;
                }else{

                    return `
                        <input type="checkbox" class="checkbox" id="${data.id}"  name="status" value="inactive" onclick="changeBucketStatus('${data.name}', '${data.id}')">
                        `;
                }
            }

        },

        {
            data: null,
            render: function(data) {
                return `<a href="/bucket/edit/${data.id}" class="btn btn-white btn-sm" ><i class="fa fa-edit"></i></a>`;
            }
        }
    ]
});


let changeBucketStatus = (name, id)=>{
    let status;


    var checked = document.getElementById(id).checked;
    if (checked) {
        status = "active"
    }else{
        status = "inactive"
    }

    console.log(name, status)


    $.ajax({
        url: `/api/v1/bucket/?bucketName=${name}&updateField=status&updateValue=${status}&updateType=set`,
        headers: {access_token: $.cookie("access_token")},
        method: "patch",

    })
        .done(d => {
            if (d) {
                swal({
                        title: "Bucket Status",
                        html: true,
                        text: "Successfully Updated Status",
                        icon: "success",
                    },
                    function () {
                        // window.location.href = "?"
                    });
            }
        })
        .fail(e => {
            let error;
            if (e.responseText) error = e.responseText
            if (e.responseJSON) error = e.responseJSON
            swal({
                title: "Bucket Status Failed",
                html: true,
                text: error,
                icon: "error",
            });
        });
}

let deleteFile = (bucketId, fileId)=>{
    if(confirm('Are you sure to delete')) {
        $.ajax({
            url: `/api/v1/file/${bucketId}/${fileId}`,
            headers: {access_token: $.cookie("access_token")},
            method: "delete",

        })
            .done(d => {
                if (d) {
                    swal({
                            title: "File Delete",
                            html: true,
                            text: "Successfully deleted File",
                            icon: "success",
                        },
                        function () {
                            window.location.href = "?"
                        });
                }
            })
            .fail(e => {
                let error;
                if (e.responseText) error = e.responseText
                if (e.responseJSON) error = e.responseJSON
                swal({
                    title: "Bucket File Failed",
                    html: true,
                    text: error,
                    icon: "error",
                });
            });
    }
}


let thresholdChart = (d)=>{
    var pdata = [
        {
            value: (d.size_used / 1073741824).toFixed(2),
            color: "#46BFBD",
            highlight: "#5AD3D1",
            label: "Used"
        },
        {
            value: (d.threshold - (d.size_used / 1073741824)).toFixed(2),
            color:"#F7464A",
            highlight: "#FF5A5E",
            label: "Remaining"
        }
    ]
    var ctxp = $("#bucketThresholdStat").get(0).getContext("2d");
    var pieChart = new Chart(ctxp).Pie(pdata);
}


let setTotalRequestBucket = (b)=>{
    let total = 0;
    let pdata = [];
    let get = 0;
    let post = 0;
    let del = 0;

        if(b.requests){
                $.ajax({
                    url: '/api/v1/request/bucket/'+b.id,
                    headers: { access_token: $.cookie("access_token") },
                    method: 'get',
                    async: false
                }).done(data=>{
                    console.log("data is ",d);
                    for(var d of data) {
                        let method = (d.method).toLowerCase();
                        if (method == "get") {
                            get++;
                        } else if (method == "post") {
                            post++;
                        } else if (method == "delete") {
                            del++;
                        }
                    }
                }).fail(e=>alert(e.responseText))
            }



    $('#total_request').html(total);

    pdata.push({
            value: get,
            color:"#F7464A",
            highlight: "#FF5A5E",
            label: "get"
        },
        {
            value: post,
            color: "#46BFBD",
            highlight: "#5AD3D1",
            label: "post"
        },
        {
            value: del,
            color: "#FDB45C",
            highlight: "#FFC870",
            label: "delete"
        })
    var ctxd = $("#requestChartBucket").get(0).getContext("2d");
    var doughnutChart = new Chart(ctxd).Doughnut(pdata);

}





$('#delete_bucket').on("click", function () {
     deleteBucket();
})

$("#add_owner").select2({
    tags: true,
    tokenSeparators: [',', ' ']
})