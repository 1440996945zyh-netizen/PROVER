package com.yy.common.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;

import com.yy.common.enums.CommonConstants;

/**
 * @author S.hirano
 * @version 1.0
 */
public class NumUtils implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * . Constructor for NumUtils.
	 */
	public NumUtils() {
		super();
	}

	/**
	 * 格式话数字
	 */
	public static String getNumberByPattern(final String number, final String pattern) {

		DecimalFormat format = new DecimalFormat(pattern);

		return format.format(Double.parseDouble(number));
	}

	/**
	 * .
	 */
	public static int parseInt(final String strNum) {

		try {
			return Integer.parseInt(strNum);
		} catch (NullPointerException ne) {
			return 0;
		} catch (NumberFormatException ne) {
			return 0;
		}
	}

	/**
	 */
	public static short parseShort(final String strNum) {

		try {
			return Short.parseShort(strNum);
		} catch (NullPointerException ne) {
			return 0;
		} catch (NumberFormatException ne) {
			return 0;
		}
	}

	/**
	 * .
	 */
	public static long parseLong(final String strNum) {

		try {
			return Long.parseLong(strNum);
		} catch (NullPointerException ne) {
			return 0;
		} catch (NumberFormatException ne) {
			return 0L;
		}
	}

	/**
	 * .
	 */
	public static float parseFloat(final String strNum) {

		try {
			return Float.parseFloat(strNum);
		} catch (NullPointerException ne) {
			return 0;
		} catch (NumberFormatException ne) {
			return 0f;
		}
	}

	/**
	 * .
	 */
	public static double parseDouble(final String strNum) {

		try {
			return Double.parseDouble(strNum);
		} catch (NullPointerException ne) {
			return 0;
		} catch (NumberFormatException ne) {
			return 0d;
		}
	}

	/**
	 * .
	 */
	public static boolean isNullOrBlank(final String str) {
		return (str == null || str.trim().length() == 0);
	}

	/**
	 * .
	 */
	public static boolean isNullOrEmpty(final String value) {

		String string = value;

		if (string == null) {

			return true;

		} else {

			while (!string.isEmpty()
					&& string.lastIndexOf(" ") == string.length() - 1) {

				string = string.substring(0, string.length() - 1);
			}
		}

		return (string.isEmpty());
	}

	/**
	 * .
	 */
	public static boolean isNullOrEmpty(final Object[] objs) {
		return (objs == null || objs.length == 0);
	}

	/**
	 * .
	 */
	public static boolean isNullOrEmpty(final Collection col) {
		return (col == null || col.isEmpty());
	}

	/**
	 * .
	 */
	public static boolean isByte(final String str) {

		//Byte byt = null;
		boolean bValid = true;

		if (isNullOrBlank(str)) {
			return false;
		}

		if (isHalfSize(str)) {
			try {
				Byte byt = new Byte(str);
			} catch (NumberFormatException ne) {
				bValid = false;
			}
			if ("NaN".equals(str)) {
				bValid = false;
			}
		} else {
			bValid = false;
		}
		return bValid;
	}

	/**
	 * .
	 */
	public static boolean isHalfSize(final String str) {

		byte[] byts = null;
		boolean bValid = true;

		if (isNullOrBlank(str)) {
			return true;
		}

		try {
			byts = str.getBytes(CommonConstants.UTF_8);
		} catch (UnsupportedEncodingException e) {
			bValid = false;
		}

        if (byts!=null && byts.length != str.length()) {
			bValid = false;
		}
		return bValid;
	}

	/**
	 * .
	 */
	public static boolean isInt(final String str) {

		Integer itg = null;
		boolean bValid = true;

		if (isHalfSize(str)) {
			try {

				itg = new Integer(str);
			} catch (NumberFormatException ne) {
				bValid = false;
			}
			if ("NaN".equals(str)) {
				bValid = false;
			}
		} else {
			bValid = false;
		}
		return bValid;
	}

	/**
	 * .
	 */
	public static boolean isInteger(final String strNum) {

		try {
			long l = Long.parseLong(strNum);
			if (l == 0) {}
			return true;
		} catch (Exception ne) {
			return false;
		}
	}

	/**
	 * .
	 */
	public static boolean isShort(final String str) {

		Short sht = null;
		boolean bValid = true;

		if (isHalfSize(str)) {

			try {
				sht = new Short(str);
			} catch (NumberFormatException ne) {
				bValid = false;
			}
			if ("NaN".equals(str)) {
				bValid = false;
			}
		} else {
			bValid = false;
		}
		return bValid;
	}

	/**
	 * .
	 */
	public static boolean isLong(final String str) {

		Long lng = null;
		boolean bValid = true;

		if (isHalfSize(str)) {

			try {
				lng = new Long(str);
			} catch (NumberFormatException ne) {
				bValid = false;
			}
			if ("NaN".equals(str)) {
				bValid = false;
			}
		} else {
			bValid = false;
		}
		return bValid;
	}

	/**
	 * .
	 */
	public static boolean isFloat(final String str) {

		Float flt = null;
		boolean bValid = true;

		try {
			flt = new Float(str);
		} catch (NumberFormatException ne) {
			bValid = false;
		}
		if ("NaN".equals(str)) {
			bValid = false;
		}
		return bValid;
	}

	/**
	 * .
	 */
	public static boolean isDouble(final String str) {

		Double dbl = null;
		boolean bValid = true;

		try {
			dbl = new Double(str);
		} catch (NumberFormatException ne) {
			bValid = false;
		}
		if ("NaN".equals(str)) {
			bValid = false;
		}
		return bValid;
	}

	/**
	 * .
	 */
	public static boolean isNumber(final String str, final int i1, final int i2) {

		if (isNullOrBlank(str)) {

			return true;

		} else {

			if (!isDouble(str)) {

				return false;

			} else {
				int comma = str.indexOf(".");
				final int length = str.length();

				if (comma == -1) {
					comma = length;
				}

				if (comma <= i1 && (length - comma - 1) <= i2) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	/**
	 * .
	 */
	public static boolean isDecimal(final String strNum) {

		try {
			double d = Double.parseDouble(strNum);

			if (d == 0) {}
			return true;
		} catch (Exception ne) {
			return false;
		}
	}

	/**
	 * . EX.1)NumTools.round( 4545.454599999991d, 3) => 4545.455<br>
	 * EX.2)NumTools.round( 4545.454599999991d, 0) => 4545.0<br>
	 * EX.3)NumTools.round( 4545.454599999991d,-3) => 5000.0<br>
	 */
	public static double round(final double val, final int pos) {

		//return roundComp(val,pos,BigDecimal.ROUND_HALF_UP);
		double p = 0, keisu = 1, keisu2 = 0, keisu3 = 0;

		keisu = Math.pow(Integer.parseInt("10"), Math.abs(pos));
		keisu2 = Math.pow(keisu, 2);
		keisu3 = Math.pow(Float.parseFloat("0.1"), Math.abs(pos) + 2L);

		if (val < 0) {
			keisu3 *= -1;
		}
		long v1 = 0;
		if (pos < 0) {
			v1 = Math.round((val + keisu3) / keisu);

			// v1 = Math.round(doubleValue / keisu);
			v1 *= keisu2;
		} else {
			v1 = Math.round((val + keisu3) * keisu);
			// v1 = Math.round(doubleValue * keisu);
		}
		p = (double) v1 / (double) keisu;
		return p;
	}

	/**
	 * . EX.1)NumTools.roundDown( 4545.454599999991d, 3) => 4545.454��Ԃ��B<br>
	 * EX.2)NumTools.roundDown( 4545.454599999991d, 0) => 4545.0��Ԃ��B<br>
	 * EX.3)NumTools.roundDown( 4545.454599999991d,-3) => 4000.0��Ԃ��B<br>
	 */
	public static double roundDown(final double val, final int pos) {

		//return roundComp(val,pos,BigDecimal.ROUND_DOWN);
		double p = 0, keisu = 1, keisu2 = 0, keisu3 = 0;

		keisu = Math.pow(Integer.parseInt("10"), Math.abs(pos));
		keisu2 = Math.pow(keisu, 2);
		keisu3 = Math.pow(Float.parseFloat("0.1"), Math.abs(pos) + 2L);

		if (val < 0) {
			keisu3 *= -1;
		}
		long v1 = 0;
		if (pos < 0) {
			v1 = (long) ((val + keisu3) / keisu);
			v1 *= keisu2;
		} else {
			v1 = (long) ((val + keisu3) * keisu);
		}
		p = (double) v1 / (double) keisu;
		return p;
	}

	/**
	 * .
	 */
	public static double roundUp(final double val, final int pos) {
		return roundComp(val, pos, BigDecimal.ROUND_UP);
	}

	/**
	 * .
	 */
	private static double roundComp(final double doubleValue, final int position, final int mode) {

		BigDecimal bd1 = BigDecimal.valueOf(Math.pow(10d, position));
		BigDecimal bd2 = null;
		if (position < 0) {
			bd2 = bd1.setScale(Math.abs(position), BigDecimal.ROUND_HALF_UP);
		} else {
			bd2 = bd1.setScale(0, BigDecimal.ROUND_HALF_UP);
		}
		BigDecimal bd3 = BigDecimal.valueOf(Math.abs(doubleValue)
                + Math.pow(Float.parseFloat("0.1"), Math.abs(position) + 2L));
		BigDecimal bd4 = bd3.multiply(bd2);
		//BigDecimal bd5 = bd4.setScale(1,BigDecimal.ROUND_HALF_UP);
		BigDecimal bd6 = bd4.setScale(0, mode); //bd5.setScale(0,mode);
		BigDecimal bd7 = null;
		if (position < 0) {
			bd7 = bd6.divide(bd2, 0, BigDecimal.ROUND_HALF_UP);
		} else {
			bd7 = bd6.divide(bd2, position, BigDecimal.ROUND_HALF_UP);
		}
		double d2 = bd7.doubleValue();
		if (doubleValue < 0) {
			d2 *= -1;
		}
		return d2;
	}

	/**
	 * val1 + val2
	 */
	public static String add(final String val1, final String val2, final String mode, final int pos) {

		try {
			String a = NumUtils.removeComma(val1);
			String b = NumUtils.removeComma(val2);

			BigDecimal deci1 = new BigDecimal(a);
			BigDecimal deci2 = new BigDecimal(b);

			//deci3 = deci1 + deci2
			BigDecimal deci3 = new BigDecimal(deci1.add(deci2).toString());

			if ("UP".equals(mode)) {
				//
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_UP);
			} else if ("DOWN".equals(mode)) {
				//
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_DOWN);
			} else if ("ROUND".equals(mode)) {
				//
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_HALF_UP);
			} else {
				//
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_UNNECESSARY);
			}
			return deci3.toString();

		} catch (Exception nfe) {
			return "0";
		}
	}
	/**
	 *  float 小数点后保留2位，第3位四舍五入
	 * @param value
	 * @return
	 */
	private static String formatDouble(double value) {

		try {

			BigDecimal bg = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);

			DecimalFormat df1 = new DecimalFormat("########.##");

			return df1.format(bg.doubleValue());

		} catch (Exception e) {
			return "0";
		}


	}
	/**
	 * Z val1 - val2
	 */
	public static String subtract(final String val1, final String val2, final String mode, final int pos) {

		try {
			String a = NumUtils.removeComma(val1);
			String b = NumUtils.removeComma(val2);

			BigDecimal deci1 = new BigDecimal(a);
			BigDecimal deci2 = new BigDecimal(b);

			//deci3 = deci1 - deci2
			BigDecimal deci3 = deci1.subtract(deci2);

			if ("UP".equals(mode)) {
				//�؂�グ
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_UP);
			} else if ("DOWN".equals(mode)) {
				//�؂�̂�
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_DOWN);
			} else if ("ROUND".equals(mode)) {
				//�l�̌ܓ�
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_HALF_UP);
			} else {
				//���̂܂�
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_UNNECESSARY);
			}

			String result = deci3.toString();

			return result;

		} catch (Exception nfe) {
			return "0";
		}
	}

	/**
	 * . ��Z val1 * val2
	 */
	public static String multiply(final String val1, final String val2, final String mode, final int pos) {

		try {
			String a = NumUtils.removeComma(val1);
			String b = NumUtils.removeComma(val2);

			BigDecimal deci1 = new BigDecimal(a);
			BigDecimal deci2 = new BigDecimal(b);

			BigDecimal deci3;

			if ("UP".equals(mode)) {
				//�؂�グ
				deci3 = deci1.multiply(deci2);
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_UP);
			} else if ("DOWN".equals(mode)) {
				//�؂�̂�
				deci3 = deci1.multiply(deci2);
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_DOWN);
			} else if ("ROUND".equals(mode)) {
				//�l�̌ܓ�
				deci3 = deci1.multiply(deci2);
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_HALF_UP);
			} else {
				//���̂܂�
				deci3 = deci1.multiply(deci2);
				deci3 = deci3.setScale(pos, BigDecimal.ROUND_UNNECESSARY);
			}

			String result = deci3.toString();

			return result;

		} catch (Exception nfe) {
			return "0";
		}
	}

	/**
	 * . val1 / val2
	 */
	public static String divide(final String val1, final String val2, final String mode, final int pos) {

		try {
			String a = NumUtils.removeComma(val1);
			String b = NumUtils.removeComma(val2);

			BigDecimal deci1 = new BigDecimal(a);
			BigDecimal deci2 = new BigDecimal(b);

			BigDecimal deci3;

			if ("UP".equals(mode)) {
				//�؂�グ
				deci3 = deci1.divide(deci2, pos, BigDecimal.ROUND_UP);
			} else if ("DOWN".equals(mode)) {
				//�؂�̂�
				deci3 = deci1.divide(deci2, pos, BigDecimal.ROUND_DOWN);
			} else if ("ROUND".equals(mode)) {
				//�l�̌ܓ�
				deci3 = deci1.divide(deci2, pos, BigDecimal.ROUND_HALF_UP);
			} else {
				//���̂܂�
				deci3 = deci1.divide(deci2, pos, BigDecimal.ROUND_UNNECESSARY);
			}

			String result = deci3.toString();

			return result;

		} catch (Exception nfe) {
			return "0";
		}
	}

	/**
	 * .
	 *
	 * @return String
	 */
	public static String numberFormat(String str) {

		String ret = "";
		final int comma = 3;

		try {

			str = (str == null) ? "" : str;

			if (str.trim().length() > 0) {
				if (str.substring(0, 1).equals(".")) {
					str = "0" + str;
				}
			}

			String tmpStr = str.replaceAll(",", "").replaceAll(" ", "").replaceAll("�@", "");

			//������
			String tmp1 = "";

			//������
			String tmp2 = "";

			if (str.indexOf(".") == -1) {
				tmp1 = tmpStr;
			} else {

				//�������Ə������ɕ�����
				tmp1 = str.substring(0, tmpStr.indexOf("."));
				tmp2 = str.substring(tmpStr.indexOf(".") + 1, tmpStr.length());
			}

			//�������ɃR���}��t���Ă�������
			StringBuffer sb = new StringBuffer();

			//3�����ɕ�����Ɖ������ł��邩
			int a = new BigDecimal(tmp1.length() / comma).intValue();

			String[] tmpstr = new String[a];

			int length = tmp1.length();

			//3�����������鏈��
			for (int i = 0; i < a; i++) {
				tmpstr[i] = tmp1.substring(length - (i + 1) * comma, length - i * comma);
			}

			//�����������̂��Ȃ��鏈��
			for (int c = 0; c < a; c++) {
				sb.append(',').append(tmpstr[a - c - 1]);
			}

			String tmptmp1 = sb.toString();
			if (length == a * comma) {
				tmptmp1 = sb.toString().substring(1, sb.toString().length());
			}

			ret = tmp1.substring(0, length - comma * a).concat(tmptmp1);

			if (str.indexOf(".") != -1) {
				ret = ret.concat(".").concat(tmp2);
			}

			// -,111  ==>  -111
			ret = ret.replaceAll("-,", "-");

		} catch (Exception e) {
			return str;
		}
		return ret;
	}

	/**
	 * .
	 */
	public static String removeComma(String str) {

		str = (str == null) ? "" : str;

		try {
			return convertToHalfSize(str).replaceAll(",", "").replaceAll(" ", "").replaceAll(" ", "");

		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * .
	 */
	public static String convertToHalfSize(final String str) {

		try {

			StringBuffer ret = new StringBuffer();
			int code = 0;
			final int length = str.length();

			for (int i = 0; i < length; i++) {
				code = str.charAt(i);

				if ((code >= 0xff01) && (code <= 0xff5d)) {
					ret.append((char) (code - 0xfee0));
				} else {
					ret.append((char) code);
				}
			}
			return ret.toString();

		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * .
	 */
	public static String roundHalfup(final String str, final int figure) {

		try {
			BigDecimal ret = new BigDecimal(str).divide(new BigDecimal("1"), figure, BigDecimal.ROUND_HALF_UP);
			return ret.toString();

		} catch (Exception e) {
			return str;
		}
	}


	/**
	 * val1 + val2
	 */
	public static BigDecimal add(final BigDecimal val1, final BigDecimal val2, final int pos) {

		try {
			BigDecimal deci1 = new BigDecimal(val1.toString());
			BigDecimal deci2 = new BigDecimal(val2.toString());

			//deci3 = deci1 + deci2
			BigDecimal deci3 = new BigDecimal(deci1.add(deci2).toString());

			return deci3.setScale(pos, BigDecimal.ROUND_UP);

		} catch (Exception nfe) {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * Z val1 - val2
	 */
	public static BigDecimal subtract(final BigDecimal val1, final BigDecimal val2, final int pos) {

		try {

			//deci3 = deci1 - deci2
			BigDecimal deci3 = val1.subtract(val2);

			return deci3.setScale(pos, BigDecimal.ROUND_UP);

		} catch (Exception nfe) {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * val1 * val2
	 */
	public static BigDecimal multiply(final BigDecimal val1, final BigDecimal val2, final int pos) {

		try {

			BigDecimal deci3 = val1.multiply(val2);

			return deci3.setScale(pos, BigDecimal.ROUND_UP);

		} catch (Exception nfe) {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * . val1 / val2
	 */
	public static BigDecimal divide(final BigDecimal val1, final BigDecimal val2, final int pos) {

		try {

			BigDecimal deci3 = val1.divide(val2, pos, BigDecimal.ROUND_HALF_UP);

			return deci3;

			// return deci3.setScale(pos, BigDecimal.ROUND_UP);

		} catch (Exception nfe) {
			return BigDecimal.ZERO;
		}
	}

	public static void main(String[] arg2) {

		//BigDecimal aa =  add((new BigDecimal(3.775)), new BigDecimal(0.937),0, 2);
		//System.out.println("=============" + aa);
		BigDecimal mData = new BigDecimal("9.655").setScale(2, BigDecimal.ROUND_HALF_UP);
        //System.out.println(new BigDecimal(3.775)).toString()));//mData=9.66
		//BigDecimal a = new BigDecimal(3.776);
		//BigDecimal b = new BigDecimal(3.776);
		System.out.println(divide(new BigDecimal(1), new BigDecimal(3), 2));


	}


}
