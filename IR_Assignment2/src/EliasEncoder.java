
public class EliasEncoder {
	
	public static String numToBinary(int num) {
		if(num == 0)
			return "0";
		
		String s = "";
		while(num > 0) {
			s = num % 2 + s;
			num /= 2;
		}
		
		return s;
	}
	
	public static int binaryToNum(String s) {
		int num = 0;
	
		for(int i = s.length() - 1; i >= 0; i--) {
			char ch = s.charAt(i);
			if(ch == '1') {
				num += (int) Math.pow(2, i);
			}
		}
		
		return num;
	}
	
	public static String getUnaryCode(int count) {
		String s = "";
		for(int i = 0; i < count; i++) {
			s += "1";
		}
		s += "0";
		
		return s;
	}
	
	public static String gammaEncode(int num) {
		if(num == 0) {
			return  "0";
		}
		
		String binary = numToBinary(num);
		String offset = binary.substring(1);
		int offsetSize = offset.length();
		String offsetUnaryCode = getUnaryCode(offsetSize);
		
		String gammaCode = offsetUnaryCode + offset;
		
		return gammaCode;
	}
	
	public static String deltaEncode(int num) {
		String binary = numToBinary(num);
		String offset = binary.substring(1);
		int binaryLen = binary.length();
		String gammaEncodedString = gammaEncode(binaryLen);
		String deltaEncodedString = gammaEncodedString + offset;
		
		return deltaEncodedString;
	}
	
	public static void EliasEncode(int gap, BitOutputStream output, boolean isGamma) {
		
		String encodedString;
		if(isGamma) {
			encodedString = gammaEncode(gap);
		} else {
			encodedString = deltaEncode(gap);
		}
		
		for(int i = encodedString.length() - 1; i >= 0; i--) {
			if(encodedString.charAt(i) == '0') {
				output.writeBit(0);
			}
			else if(encodedString.charAt(i) == '1') {
				output.writeBit(1);
			}
		}
	}
	
	public static void main(String args[]) {
		for(int i = 1; i < 11; i++) {
			System.out.println("Gamma " + gammaEncode(i) + " delta " + deltaEncode(i));
		}
	}

}
