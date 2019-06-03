
const core = {};

core.form = {
    get: function(form) {
    var $form = $(form);
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

$.map(unindexed_array, function(n, i) {
indexed_array[n["name"]] = n["value"];
});
return indexed_array;
},

    getV2: function (form) {
        var indexed_array = {};
        let forms = [];
        $(form).each(function(){
            forms = $(this).find(':input') //get all form elements
        });

        var i = 0;
        for(var f of forms){
            i++;
            f = $(f);

            if(f.attr("type") == "checkbox"){
                var checkbox = [];

                $(`input:checkbox[name=${f.attr("name")}]:checked`).each(function(){
                    checkbox.push($(this).val());
                });

                indexed_array[f.attr("name")] = checkbox;
            }
             else {

                indexed_array[f.attr("name")] = f.val();
            }
        }

        Object.keys(indexed_array).forEach(key =>{
            if(key === undefined || key === "undefined" || key === null || !key){
                delete indexed_array[key] }
        });

        return indexed_array;
    },

set: function(form, data, fields) {
    if (!fields) {
        console.error("Must send field list of fill");
        return;
    }
    fields = fields.split(",");
    _.each(fields, function(f){
        $(`${form} input[name=${f}]`).val(data[f]);
        $(`${form} select[name=${f}]`).val(data[f]);
        $(`${form} textarea[name=${f}]`).val(data[f]);
});
}

}


core.session = {
    getToken: function(){
    return { access_token: $.cookie("access_token") };
},
getUser: function(){
    var userStr = $.cookie("user");
    if (userStr) return JSON.parse(userStr);
    else return {};
}
};

let bucketsHtml = (b)=>{
  return b.map(e=>{
       return `<li><a class="treeview-item" href="/bucket/${e.name}"><i class="icon fa fa-circle-o"></i>
               ${e.name}
                </a></li>`;
   }).join('')
}
core.sidebar = {
    set: function () {
         $.ajax({
           url: '/api/v1/user/',
             headers: { access_token: $.cookie("access_token") },
             method: 'get'
         }).done(d=>{
             // console.log("user is ",d);
             if(d.id) {
                 $('.user_name').text(d.name.user);
                 $('.user_buckets').html(bucketsHtml(d.buckets));
                 if(d.role.role === "admin"){
                     $('#plan_create').show();
                     $("#administration").show();
                 }
             }
         }).fail(e=>alert(e.responseText))
    }
}

core.sidebar.set();
