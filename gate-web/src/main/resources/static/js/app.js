$(function(){
	$('[data-toggle="tooltip"]').tooltip();

	$('.popover-hover').popover({
	  trigger: 'hover',
	  html: true
	});

	$('.menu_toggle').click(function(){
		$('body').toggleClass('view_in');
	});

	$('.accordion .btn-link').click(function(){
		$(this).parents('.card').toggleClass('active');
		$(this).parents('.card').siblings().removeClass('active');
	});

	$('[dismiss-popup]').click(function () {
		$(this).parents('.card-popup').remove();
	})

	$('.btn-group-toggle .btn').click(function(){
		$(this).siblings().removeClass('active');
		$(this).toggleClass('active');
	})

    $('.modal').on('shown.bs.modal', function () {
	    $('[autofocus]').focus();
	    // setFormHeight();
	}) 

    $('.data_list').click(function(){
    	$(this).siblings().removeClass('active')
    		.end()
    		.addClass('active');
    });
    $('.dropdown-menu-picker, .dropdown-filter').on('click', function(event){
	    event.stopPropagation();
	});
	$('#datePicker').datepicker({
		todayBtn: true,
		todayHighlight: true
	});
	$('#platform, #platform2, #platform3, #platform4').msDropDown();

	$(".custom-file-input").on("change", function() {
	  var fileName = $(this).val().split("\\").pop();
	  $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
	});

	$('.collapse').on('shown.bs.collapse', function () {
	  // setFormHeight();
	  $(this).siblings().find('.fas').toggleClass('fa-caret-right fa-caret-down');
	}).on('hidden.bs.collapse', function () {
	  // setFormHeight();
	  $(this).siblings().find('.fas').toggleClass('fa-caret-down fa-caret-right');
	})

	$('.btn-toggle').click(function(){
		if($(this).hasClass('btn-C')){
			$(this).toggleClass('text-danger btn-danger')
		}
		if($(this).hasClass('btn-W')){
			$(this).toggleClass('text-warning btn-warning')
		}
	});

	$('.smartwizard').smartWizard({
		selected: 0,
		theme: 'dots',
		autoAdjustHeight:true,
		transitionEffect:'fade',
		showStepURLhash: false,
		lang: { 
          next: 'Next',
          previous: 'Back'
      },
	});
});