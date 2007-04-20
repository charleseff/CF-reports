/**
 * $Author: charles $
 * $Revision: 8555 $
 * $Date: 2006-04-13 01:11:03 -0400 (Thu, 13 Apr 2006) $
 **/

function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
        window.onload = func;
    } else {
        window.onload = function() {
            oldonload();
            func();
        }
    }
}

function getSelectedRadio(buttonGroup) {
   // returns the array number of the selected radio button or -1 if no button is selected
   if (buttonGroup[0]) { // if the button group is an array (one button is not an array)
      for (var i=0; i<buttonGroup.length; i++) {
         if (buttonGroup[i].checked) {
            return i
         }
      }
   } else {
      if (buttonGroup.checked) { return 0; } // if the one button is checked, return zero
   }
   // if we get to this point, no radio button is selected
   return -1;
} // Ends the "getSelectedRadio" function

function getSelectedRadioValue(buttonGroup) {
   // returns the value of the selected radio button or "" if no button is selected
   var i = getSelectedRadio(buttonGroup);
   if (i == -1) {
      return "";
   } else {
      if (buttonGroup[i]) { // Make sure the button group is an array (not just one button)
         return buttonGroup[i].value;
      } else { // The button group is just the one button, and it is checked
         return buttonGroup.value;
      }
   }
} // Ends the "getSelectedRadioValue" function

function removeAllChildNodes(node) {
    if (node && node.hasChildNodes && node.removeChild) {
        while (node.hasChildNodes()) {
            node.removeChild(node.firstChild);
        }
    }

}

function trimString (str) {
    str = this != window? this : str;
    return str.replace(/^\s+/g, '').replace(/\s+$/g, '');
}