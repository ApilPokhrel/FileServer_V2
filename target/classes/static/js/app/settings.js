

$(document).on("submit", "#plan_create", function (event) {
    event.preventDefault();
    createPlan("plan_create");
});
function createPlan(form){

    let feature =$('[name=features]').val().toString();
    let backup = "no";
    if($(`input:checkbox[name=backup]:checked`).length > 0){
       backup = "yes";
    }
    console.log(feature);
    let data = {
        name : $('[name=name]').val(),
        feature,
        size: parseInt($('[name=size]').val()),
        backup,
        requests: parseInt($('[name=request]').val()),
        timeGap:  parseInt($('[name=timeGap]').val()),
        bucket:  parseInt($('[name=bucket]').val()),
        price:  parseInt($('[name=price]').val()),
        type: $('[name=type]').val(),
        description: $('[name=description]').val()


    }
    $.ajax({
        url: `/api/v1/settings/plan`,
        headers: { access_token: $.cookie("access_token") },
        method: "post",
        data,
    })
        .done(d => {
            if(d){
                swal({
                    title: "Plan Create",
                    html: true,
                    text: "Successfully created Plan",
                    icon: "success",
                }, function () {
                    window.location.href = "?";
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
                title: "Plan Create Failed",
                html: true,
                text: error,
                icon: "error",
            });
        });
}


function getPlans() {
    $.ajax({
        url: '/api/v1/settings/plan',
        headers: { access_token: $.cookie("access_token") },
        method: 'get'
    }).done(d=>{
        if(d){
            $('#wrapper-card').html(planHtml(d));
        }
    }).fail(e=>console.log((e.responseText)));
}


let planHtml = (plans)=>{
    return plans.map(e=>{
        return `<div class="card popular">
            <div class="card-ribbon">
                <span>${e.type}</span>
            </div>
            <div class="card-title">
                <h3>${e.name}</h3>
                <h4>${e.description || ''}</h4>
            </div>
            <div class="card-price">
                <h1>
                <sup>$</sup>
                ${e.price}
                <small>month</small>
            </h1>
            </div>
             <h4>Total buckets ${e.bucket || 1}</h4>
            <div class="card-description">
                <ul>
                   ${featuresHtml(e.features)}
                </ul>
            </div>
            <div class="card-action">
                <button type="button">Get ${e.name}</button>
            </div>
        </div>`;
    }).join("")
}


let featuresHtml = (ft)=>{
    return ft.map(f=>{
       return `<li>${f}</li>`;
    }).join("")
}

getPlans();