var Pc700PrintLoader = function (require, exports, module) {
    
    var exec = require('cordova/exec');
    
    var Pc700Print = function () {
	
    };
    
    Pc700Print.prototype.printText = function (successCallback, errorCallback) {
        
        if (successCallback === null) {
            successCallback = function (response) {
                console.log('Pc700Print.printText sukses: ' + response);
            };
        }
        
        if (errorCallback === null) {
            errorCallback = function (error) {
                console.error('Pc700Print.printText deshtim: ' + error);
            };
        }
        
        if (typeof errorCallback != "function") {
            console.error("Pc700Print.printText failure: parametri deshtimit nuk eshte funksion");
            return;
        }
        
        if (typeof successCallback != "function") {
            console.error("Pc700Print.printText failure: parametri callback i suksesit duhet te jete patjeter funksion");
            return;
        }
        
        exec(
            successCallback,
            errorCallback,
            "Pc700Print",
            "printText",
            ["Ridi", 0]
        );
    };
    
    module.exports = new Pc700Print();
};

Pc700PrintLoader(require, exports, module);
cordova.define("cordova/plugin/Pc700Print", Pc700PrintLoader);
