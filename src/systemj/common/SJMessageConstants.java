/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common;

/**
 *
 * @author Udayanto
 */
public class SJMessageConstants {
    
    private SJMessageConstants(){
        
    }
    
    public enum MessageType{
        
        SINGLECON(0),
        
        NON(1),
        
        ACK(2),
        
        RST(3),
        
        DUALCON(4);
        
        public final int val;
        
        MessageType(int val){
            this.val = val;
        }
        
        public static MessageType valueOf(int val){
            
            switch (val){
                case 0: return SINGLECON;
                case 1: return NON;
                case 2: return ACK;
                case 3: return RST;
                case 4: return DUALCON;
                default: throw new IllegalArgumentException("Unknown Message Type");
            }
            
            
        }
        
        
    }
    
    public enum MessageCode {
        
        GET(1),
        
        POST(2);
        
        //PUT(3),
        
        //DELETE(4);
        
        
        public final int val;
        
        MessageCode(int val){
            this.val = val;
        }
        
        public static MessageCode valueOf(int val){
            
            switch (val){
                case 1: return GET;
                case 2: return POST;
                //case 3: return PUT;
                //case 4: return DELETE;
                default: throw new IllegalArgumentException("Unknown Message Code");
            }
            
        }
        
    }
    
    /*
    public enum ResponseCode{
        
        // Success
		CREATED(65),
		DELETED(66),
		VALID(67),
		CHANGED(68),
		CONTENT(69),
		CONTINUE(95),

		// Client error
		BAD_REQUEST(128),
		UNAUTHORIZED(129),
		BAD_OPTION(130),
		FORBIDDEN(131),
		NOT_FOUND(132),
		METHOD_NOT_ALLOWED(133),
		NOT_ACCEPTABLE(134),
		REQUEST_ENTITY_INCOMPLETE(136),
		PRECONDITION_FAILED(140),
		REQUEST_ENTITY_TOO_LARGE(141), 
		UNSUPPORTED_CONTENT_FORMAT(143),

		// Server error
		INTERNAL_SERVER_ERROR(160),
		NOT_IMPLEMENTED(161),
		BAD_GATEWAY(162),
		SERVICE_UNAVAILABLE(163),
		GATEWAY_TIMEOUT(164),
		PROXY_NOT_SUPPORTED(165);
		
		// The code value. 
		public final int value;
                
                private ResponseCode(int value) {
			this.value = value;
		}
                
                public static ResponseCode valueOf(int val){
            
                    switch (val){
                                case 65: return CREATED;
				case 66: return DELETED;
				case 67: return VALID;
				case 68: return CHANGED;
				case 69: return CONTENT;
				case 128: return BAD_REQUEST;
				case 129: return UNAUTHORIZED;
				case 130: return BAD_OPTION;
				case 131: return FORBIDDEN;
				case 132: return NOT_FOUND;
				case 133: return METHOD_NOT_ALLOWED;
				case 134: return NOT_ACCEPTABLE;
				case 136: return REQUEST_ENTITY_INCOMPLETE;
				case 140: return PRECONDITION_FAILED;
				case 141: return REQUEST_ENTITY_TOO_LARGE;
				case 143: return UNSUPPORTED_CONTENT_FORMAT;
				case 160: return INTERNAL_SERVER_ERROR;
				case 161: return NOT_IMPLEMENTED;
				case 162: return BAD_GATEWAY;
				case 163: return SERVICE_UNAVAILABLE;
				case 164: return GATEWAY_TIMEOUT;
				case 165: return PROXY_NOT_SUPPORTED;
				default: // Make an extensive search
					for (ResponseCode code:ResponseCode.values())
						if (code.value == val) return code;
					throw new IllegalArgumentException("Unknown message response code "+val);
                    }
            
                }
        
    }
    */
    
    
}
