/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var obj;
var argUser;
var newUserVM;
var newUserContainer;
var noVNCHeading;
var iframe;
var msg;
/* array of launched VM */

$(function () {
    newUserVM = $("#newUserVM");
    newUserContainer = $("#newUserContainer");
    noVNCHeading = $("#noVNCHeading");
    iframe = $("#noVNCIframe");
    msg = $("#noVNCAlert");
    newUserVM.hide();
    newUserContainer.hide();
    noVNCHeading.hide();

    /* Kill all old process to VNC */
//    $.ajax({
//        url: "../machine/resetvnc",
//        async: true,
//        success: function (data, textStatus, jqXHR) {
//            console.log("resetvnc : OK");
//            iframe.hide();
//            msg.show();
//            noVNCHeading.hide();
//        },
//        dataType: "json"
//    });


    /* Getting user's data from the server */
    $.ajax({
        url: "../machine/list",
        data: 0,
        success: function (data, textStatus, jqXHR) {
            console.log("DATA IS : " + JSON.stringify(data));
            obj = jQuery.parseJSON(JSON.stringify(data));
            updateMachines();
        }
    });



    $("#containerConstructor").change(function () {
        var optionSelected = $("option:selected", this);
        // LXC selected

        if (optionSelected.val() === "lxc") {
            console.log("TAMAYERE");
            $("#containerType").val("Debian");
            $("#containerType").prop('disabled', 'disabled');
        } else {
            $("#containerType").val("");
            $("#containerType").prop('disabled', false);
        }
    });

    $("#buttonNewContainer").click(function () {
        console.log("Trying to create new container.");
        $.ajax({
            url: "../machine/create",
            data:
                    {
                        name: $("#containerName").val(),
                        kind: $('#containerConstructor').find(":selected").val(),
                        img: $('#containerType').val()
                    },
            success: function (data, textStatus, jqXHR) {
                if (data.code === 200) {
                    console.log("The container was created.");
                    $("#errorContainer").text("Successfully created!");
                    $("#errorContainer").css('color', 'green');
                    obj.list.push({name: $("#containerName").text(), kind: $('#containerConstructor').find(":selected").val()});
                    updateMachines();
                } else {
                    console.log("[ERROR] The container could not be created.\n" + data.error);
                    $("#errorContainer").text(data.error);
                    $("#errorContainer").css('color', 'red');
                }
            }
        });

    });

    $("#buttonNewVM").click(function () {
        console.log("Trying to create new VM.");
        $.ajax({
            url: "../machine/create",
            data:
                    {
                        name: $('#VMName').val(),
                        kind: $('#VMConstructor').find(":selected").val(),
                        memorySize: $('#VMRAM').find(":selected").val(),
                        hddSize: $('#VMSpace').find(":selected").val(),
                        img: $('#VMType').find(":selected").val()
                    },
            success: function (data, textStatus, jqXHR) {
                if (data.code === 200) {
                    console.log("The VM was created.");
                    $("#errorVM").text("Successfully created!");
                    $("#errorVM").css('color', 'green');
                    obj.list.push({name: $("#VMName").text(), kind: $('#VMConstructor').find(":selected").val()});
                    updateMachines();
                } else {
                    console.log("[ERROR] The VM could not be created.\n" + data.error);
                    $("#errorVM").text(data.error);
                    $("#errorVM").css('color', 'red');
                }
            }
        });

    });

    $("#buttonNewUserContainer").click(function () {
        console.log("Trying to add user to the container: " + obj.list[argUser].NAME);
        $.ajax({
            url: "../machine/add",
            data:
                    {
                        name: obj.list[argUser].NAME,
                        username: $("#userContainer").val(),
                        password: $("#passwordContainer").val()
                    },
            success: function (data, textStatus, jqXHR) {
                if (data.code === 200) {
                    console.log("The user was added.");
                    newUserContainer.hide();
                } else {
                    console.log("[ERROR] The user could not be added.\n" + data.error);
                    $("#errorNewUserContainer").text(data.error);
                    $("#errorNewUserContainer").css('color', 'red');
                }
            }
        });
    });

    $("#buttonNewUserVM").click(function () {
        console.log("Trying to add user to the VM: " + obj.list[argUser].NAME);
        $.ajax({
            url: "../machine/add",
            data:
                    {
                        name: obj.list[argUser].NAME,
                        username: $("#userVM").val(),
                        password: $("#passwordVM").val()
                    },
            success: function (data, textStatus, jqXHR) {
                if (data.code === 200) {
                    console.log("The user was added.");
                    newUserVM.hide();
                } else {
                    console.log("[ERROR] The user could not be added.\n" + data.error);
                    $("#errorNewUserVM").text(data.error);
                    $("#errorNewUserVM").css('color', 'red');
                }
            }
        });
    });

});

/* Function used to turn machine arg on or off */
function onoff(arg) {
    console.log("Enters onoff function with argument: " + arg);

    console.log("Asking machine " + obj.list[arg].NAME + " state to the server.");
    $.ajax({
        url: "../machine/isOn",
        data:
                {
                    name: obj.list[arg].NAME
                },
        success: function (data, textStatus, jqXHR) {
            if (data === "on") {
                console.log("Server responded on.");
                console.log("Trying to turn machine off.");
                $.ajax({
                    url: "../machine/turnOff",
                    data:
                            {
                                name: obj.list[arg].NAME
                            },
                    success: function (data, textStatus, jqXHR) {
                        if (data.code === 200)
                            console.log("Machine turned off.");
                        else
                            console.log("[ERROR] Machine did not turned off.\n" + data.error);
                    },
                    dataType: "json"
                });
            } else {
                console.log("Server responded off.");
                console.log("Trying to turn machine on.");
                $.ajax({
                    url: "../machine/turnOn",
                    data:
                            {
                                name: obj.list[arg].NAME
                            },
                    success: function (data, textStatus, jqXHR) {
                        if (data.code === 200)
                            console.log("Machine turned on.");
                        else
                            console.log("[ERROR] Machine did not turned on.\n" + data.error);
                    }
                });
            }
        }
    });
}

/* Function used to delete machine arg */
function deletion(arg) {
    console.log("Enters delete function with argument: " + arg);

    console.log("Trying to delete machine " + obj.list[arg].NAME + ".");
    $.ajax({
        url: "../machine/delete",
        data:
                {
                    name: obj.list[arg].NAME
                },
        success: function (data, textStatus, jqXHR) {
            if (data.code === 200) {
                console.log("Machine was deleted.");
                obj.list.splice(arg, 1);
                updateMachines();
            } else {
                console.log("[ERROR] Machine could not be deleted.\n" + data.error);
            }
        },
        dataType: "json"
    });
}

/* Function used to use ssh on arg machine */
function ip(arg) {
    console.log("Enters ssh function with argument: " + arg);

    console.log("Trying to get IP of machine " + obj.list[arg].NAME + ".");
    $.ajax({
        url: "../machine/IP",
        data:
                {
                    name: obj.list[arg].NAME
                },
        success: function (data, textStatus, jqXHR) {
            if (data.code === 200) {
                console.log("IP was received.");

                console.log(data.IP);
                /* TODO */
                /* DES */
                /* FAMILLES */
                alert("The IP address of " + obj.list[arg].NAME + " is " + data.IP);
            } else {
                console.log("[ERROR] IP not received.\n" + data.error);
            }
        },
        dataType: "json"
    });
}

function vnc(arg) {
    console.log("Enters vnc function with argument: " + arg);

    console.log("Trying to get vnc of machine " + obj.list[arg].NAME + ".");
    $.ajax({
        url: "../machine/enablevnc",
        data:
                {
                    name: obj.list[arg].NAME
                },
        success: function (data, textStatus, jqXHR) {
            if (data.code === 200) {
                console.log("VNC was received.");

                console.log(data.vnc);
                /* TODO */
                /* DES */
                /* FAMILLES */
                if (data.vnc < 10)
                    alert("The VNC port used by " + obj.list[arg].NAME + " is 590" + data.vnc);
                else
                    alert("The VNC port used by " + obj.list[arg].NAME + " is 59" + data.vnc);

            } else {
                console.log("[ERROR] VNC not received.\n" + data.error);
            }
        },
        dataType: "json"
    });
}

/**
 * launch vnc of the arg identifier VM
 * execute python script to launch mini-web server need to make the link between VM (vnc producer) and iframe (vnc consumer)
 * @param {type} arg : id of VM
 * @returns {undefined}
 */
function launchVnc(arg) {
    console.log("JS launchVNC : begin");
    vncPort = "-99";

    // Get vnc port of VM
    $.ajax({
        url: "../machine/enablevnc",
        async: false,
        data:
                {
                    name: obj.list[arg].NAME
                },
        success: function (data, textStatus, jqXHR) {
            if (data.code === 200) {
                if (data.vnc < 10)
                    vncPort = "590" + data.vnc;
                else
                    vncPort = "59" + data.vnc;
                console.log("JS launchVNC in AJAX : vncPort - " + vncPort);
            } else {
                console.log("[ERROR] VNC not received.\n" + data.error);
            }
        },
        dataType: "json"
    });


    $.ajax({
        url: "../machine/launchvnc",
        async: false,
        data: {vncPort: vncPort},
        success: function (data, textStatus, jqXHR) {
            console.log("Launch vnc : OK \tLaunch showPort : " + data.showPort + "\tLaunch vncPort : " + data.vncPort);
            alert("VNC is launch on port : " + data.vncPort);
            iframe.attr("src", "http://localhost:" + data.showPort + "/vnc.html")
            noVNCHeading.text(obj.list[arg].NAME + " is launch on noVNC")

            iframe.show();
            msg.hide();
            noVNCHeading.show();
        },
        dataType: "json"
    });

}


/* Function used to enable ssh on arg machine */
function enableSSH(arg) {
    console.log("Enters ENABLE ssh function with argument: " + arg);

    console.log("Trying to enable SSH for machine " + obj.list[arg].NAME + ".");
    $.ajax({
        url: "../machine/enablessh",
        data:
                {
                    name: obj.list[arg].NAME
                },
        success: function (data, textStatus, jqXHR) {
            if (data.code === 200) {
                console.log("SSH was enabled.");

                /* TODO */
                /* DES */
                /* FAMILLES */

            } else {
                console.log("[ERROR] SSH was not enabled.\n" + data.error);
            }
        },
        dataType: "json"
    });
}


/* Function used to set which machine the user will be added to */
function addUser(arg, type) {
    argUser = arg;

    /* If it's a VM */
    if (type) {
        newUserVM.show();
        /* If it's a container */
    } else {
        newUserContainer.show();
    }
}


/* function used to update the html code showing machine owned by the current user */
function updateMachines() {
    var htmlListVM = '<ul class="list-group">';
    var htmlListContainer = '<ul class="list-group">';

    for (var i = 0; i < obj.list.length; ++i) {

        var type = obj.list[i].KIND_VM === 0 || obj.list[i].KIND_VM === 1;

        var toAddCTN = '<li class="list-group-item d-flex justify-content-between align-items-center">';
        var toAdd = '<li class="list-group-item d-flex justify-content-between align-items-center">';
        toAddCTN += obj.list[i].NAME;
        toAddCTN += '<div class="pull-right"><div class="btn-group"><button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">';
        toAddCTN += 'Actions<span class="caret"></span></button><ul class="dropdown-menu pull-right" role="menu">';

        /* On/Off */
        toAddCTN += '<li><a href="#" onclick="onoff(' + i + ')" >On/Off</a></li>';

        /* Delete */
        toAddCTN += '<li><a href="#" onclick="deletion(' + i + ')" >Delete</a></li>';

        /* IP */
        toAddCTN += '<li><a href="#" onclick="ip(' + i + ')" >Get Ip adress</a></li>';

        /* Enable SSH */
        toAddCTN += '<li><a href="#" onclick="enableSSH(' + i + ')" >Enable SSH</a></li>';

        /* Add User */
        toAddCTN += '<li class="addUserContainerLi"><a href="#" onclick="addUser(' + i + ', ' + type + ')" >Add user</a></li></ul></div></div></li>';




        toAdd += obj.list[i].NAME;
        toAdd += '<div class="pull-right"><div class="btn-group"><button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">';
        toAdd += 'Actions<span class="caret"></span></button><ul class="dropdown-menu pull-right" role="menu">';

        /* On/Off */
        toAdd += '<li><a href="#" onclick="onoff(' + i + ')" >On/Off</a></li>';

        /* Delete */
        toAdd += '<li><a href="#" onclick="deletion(' + i + ')" >Delete</a></li>';

        /* GET VNC */
        toAdd += '<li><a href="#" onclick="vnc(' + i + ')" >Get VNC port</a></li>';

        /* Active VNC */
        toAdd += '<li><a href="#" onclick="launchVnc(' + i + ')" >Launch VNC port</a></li>'

        /* Close list */
        toAdd += '</ul></div></div></li>';

        /* Adding to correct part */
        if (type) {
            htmlListVM += toAdd;
        } else {
            htmlListContainer += toAddCTN;
        }
    }

    htmlListVM += '</ul>';
    htmlListContainer += '</ul>';


    $("#handlingVMList").html(htmlListVM);
    $("#handlingContainerList").html(htmlListContainer);
}


$(function () {
    var newUserVM = $("#newUserVM");
    var newUserContainer = $("#newUserContainer");
    newUserVM.hide();
    newUserContainer.hide();

    $("#containerConstructor").change(function () {
        var optionSelected = $("option:selected", this);
        // LXC selected
        if (optionSelected.val() == 2) {
            $("#containerType").val("Debian")
            $("#containerType").prop('disabled', 'disabled');
        } else {
            $("#containerType").val("");
            $("#containerType").prop('disabled', false);
        }
    });


    $(".addUserVMLi").click(function () {
        newUserVM.show();
    });

    $("#buttonNewUserVM").click(function () {
        newUserVM.hide();
    });

    $(".addUserContainerLi").click(function () {
        newUserContainer.show();
    });

    $("#buttonNewUserContainer").click(function () {
        newUserContainer.hide();
    });




});
