console.log("this is script file");

const toggleSidebar = () => {
    if($(".sidebar").is(":visible")){
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
        //true band krna hai

    }
    else{ 
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
        //false show krna hai
    }
};

// search js
const search = () => {

    let query=$("#search-input").val();

    if(query =="")
    {
        $(".search-result").hide();
    }
    else{
       /* console.log(query);*/

        //sending request to server
        let url = `http://localhost:8080/search/${query}`;

        fetch(url)
        
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                //data........(parameter) data:any
                // console.log(data);

                let text=`<div class='list-group'>`;
                
                data.forEach((contact) => {
                    text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-action'>${contact.name} </a>`
                });

                text+=`</div>`;

                $(".search-result").html(text);
                 $(".search-result").show();


            });
        $(".search-result").show();
    }
};