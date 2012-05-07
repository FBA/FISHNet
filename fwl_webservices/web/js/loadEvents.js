/* 
 * This function allows you to add an event to the page load without worrying
 * about overwriting any existing page events.
 *
 * To add an event to the page load simply call this function and pass it your
 * function as an argument.
 *
 * eg. addLoadEvent(newFunctionToBeCalledAtPageLoad);
 *
 */
function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
        window.onload = func;
    } else {
        window.onload = function() {
            if (oldonload) {
                oldonload();
            }
            func();
        }
    }
}

