


var keysHtml = (keys)=>{
    if(keys) {
        return keys.map(k => {
            return `<option selected value="${k}">${k}</option>`;
        }).join('');
    }
}
function setUserDetail() {
    $.ajax({
        url: '/api/v1/user/',
        headers: { access_token: $.cookie("access_token") },
        method: 'get'
    }).done(d=>{
        if(d.id) {
           $('#user_name').val(d.name.user);
           $('#user_key').html(keysHtml(d.key));
            $('#user_email').val(d.contact[0].address);
            if(d.contact[1]) {
                $('#user_phone').val(d.contact[1].address);
            }
            $("#user_id").val(d.id);
            setTotalBucket(d.buckets);
             setTotalFileBackups(d.buckets);
             setTotalUploads(d.buckets);
             setTotalRequest(d.buckets);
             setStorageStat(d.buckets);

        }
    }).fail(e=>console.log((e.responseText)));
}


let myKeysHtml = (d)=>{
    return d.map(e=>{

        return `<div id="pop"></div> <div class="form-group">
                    <div class="form-group">
                      <div class="input-group">
                        <input  class="form-control" type="text" value="${e}" autocomplete="off" autocorrect="off" autocapitalize="off" onclick="keyPop('${e}')">
                        <div class="input-group-append"><button class="input-group-text" style="cursor: pointer" onclick="deleteKey('${e}')"><i class="fa fa-trash"></i></button></div>
                      </div>
                    </div>
                  </div>`
    }).join("<br>")
}


let setMyKeys = ()=>{
    $.ajax({
        url: '/api/v1/user/keys',
        headers: { access_token: $.cookie("access_token") },
        method: 'get'
    }).done(d=>{
        if(d) {
            $('#my_keys').html(myKeysHtml(d));
        }
    }).fail(e=>console.log(e.responseText))
}




let deleteKey = (key)=>{
    if(confirm("Are you sure to delete ?")) {
        $.ajax({
            url: `/api/v1/user/key/${key}`,
            headers: {access_token: $.cookie("access_token")},
            method: 'delete'
        }).done(d => {
            window.location.href = "?"
        }).fail(e => alert(e.responseText))
    }
}

$("#create_key").on("click", function () {
    createKey();
})
let createKey = ()=>{
    if(confirm("Are you sure create new one ?")) {
        $.ajax({
            url: `/api/v1/user/key`,
            headers: {access_token: $.cookie("access_token")},
            method: 'post'
        }).done(d => {
            window.location.href = "?"
        }).fail(e => alert(e.responseText))
    }
}
setMyKeys();

let keyPop = (key)=>{
    if($('.popover').length == 0) {
        $('#pop').html(`<div class="popover fade show bs-popover-top" role="tooltip" id="popover603847" style="will-change: transform; position: absolute; transform: translate3d(222px, 10px, 0px); top: 0px; left: 0px;" x-placement="top">
            <div class="arrow" style="left: 125px;"> <button class="btn btn-white" onclick="copyText('${key}')"><i class="fa fa-clipboard" aria-hidden="true"></i>
</button></div><h3 class="popover-header">key</h3>
            <div class="popover-body"><textarea id="${key}" autocomplete="off" autocorrect="off" autocapitalize="off">${key}</textarea></div>
            </div>`);
    }else{
        $('#pop').html('');

    }
}

function copyText(key) {
    var copyText = document.getElementById(key);
    console.log(copyText);
    copyText.select();
    document.execCommand("copy");
    alert("Copied");
}

let updateUser = (form)=>{

    $.ajax({
        url: '/api/v1/user/',
        headers: { access_token: $.cookie("access_token") },
        method: 'patch',
        data: core.form.get(`#${form}`)
    }).done(d=>{
        alert(d);
        window.location.href = "?";
    }).fail(e=>alert(e.responseText))
}

let setTotalBucket = (buckets)=>{
   $('#total_buckets').html(buckets.length)
}

let setTotalUploads = (buckets)=>{
    var len = 0
    for(var b of buckets){
       if(b.files) {
           len = len + b.files.length;
       }
        $('#total_files').html(len)
    }
}

let setTotalFileBackups = (buckets)=>{
    var len = 0;
    for(var b of buckets){
        if(b.backupFiles) {
            len = len + b.backupFiles.length;
        }

    }
    $('#total_backups').html(len);
}

let setStorageStat = (buckets)=>{
    let totalSize = 0;
    let sizeUsed = 0;
    for(var b of buckets){
        totalSize  = totalSize + (b.threshold * 1073741824);
        sizeUsed = sizeUsed + b.size_used;
    }

    var sdata = [
        {
            value: ((totalSize - sizeUsed) / 1073741824).toFixed(2),
            color:"#F7464A",
            highlight: "#FF5A5E",
            label: "Remaining Storage (gb)"
        },
        {
            value: (sizeUsed / 1073741824).toFixed(2),
            color: "#46BFBD",
            highlight: "#5AD3D1",
            label: "Used Storage (gb)"
        }
    ]

    var stxp = $("#total_size").get(0).getContext("2d");
    var storageChart = new Chart(stxp).Pie(sdata);

}


let setTotalRequest = (buckets)=>{
    let total = 0;
    let pdata = [];
    let get = 0;
    let post = 0;
    let del = 0;

    for(var b of buckets){

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
    }

    total = get + post + del;
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

    $('#total_request').html(total);


    var ctxd = $("#requestChart").get(0).getContext("2d");
    var doughnutChart = new Chart(ctxd).Doughnut(pdata);
}

$('#user_update').on("submit",function (event) {
      event.preventDefault();
      updateUser('user_update');
});



var index = 1;
$("#userTable").DataTable({

    pageLength: 25,
    processing: true,
    responsive: true,
    filter: true,
    sort: true,
    bFilter: true,
    searching: true,
    paging: true,
    info: true,
    // serverSide: true,
    ajax: {
        url: '/api/v1/user/list',
        type: 'GET',
        beforeSend: function (request) {
            request.setRequestHeader("access_token", $.cookie("access_token"));
        },
        dataSrc:""
    },
    // dom: "<'row'<'col-sm-12'tr>>" + "<'row'<'col-sm-4'i><'col-sm-8'<'float-right p-2'p>>>",


    columns: [
        {
            data: {},
            render: function(data, type, full) {
                return index++;
            }
        },
        {
            data: "name",
            render: function (name) {
               return name.user;
            }

        },
        {
            data: "contact",
            render: function (contact) {
                return contact[0].address;
            }

        },
        {
            data: "buckets",
            render: function (buckets) {
                return (buckets)? buckets.length : 0;
            }

        },
        {
            data: "verified",

        },
        {
            data: {},
            render: function (data) {
                console.log(data.status, data.id)
                if(data.status == null || data.status == "active"){
                  return `
                        <input type="checkbox" class="checkbox" id="status" checked name="status" value="active" onclick="changeStatus('${data.id}')">
                     `;
                }else{

                   return `
                        <input type="checkbox" class="checkbox" id="status"  name="status" value="inactive" onclick="changeStatus('${data.id}')">
                        `;

                }
            }

        },

        {
            data: {id: "_id", name: "name"},
            render: function(data) {
                return `<a href="/user/edit/${data.id}" class="btn btn-white btn-sm" ><i class="fa fa-edit"></i></a>`;
            }
        }
    ]
});

let changeStatus = (id)=>{
   $.ajax({
     url:'/api/v1/user/status/'+id,
       headers:{access_token: $.cookie("access_token")},
       method: 'patch',
   }).done(d => {
       if(d){
           swal("changed");
       }
   }).fail(e =>{
       swal(`Error \n ${e.responseText}`);
   })
}
setUserDetail();