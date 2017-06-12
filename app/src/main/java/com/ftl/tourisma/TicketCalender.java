package com.ftl.tourisma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.adapters.SlotAdapter;
import com.ftl.tourisma.custom_views.CalendarGridView;
import com.ftl.tourisma.minterface.SlotSelectListner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by skpissay on 6/8/2017.
 */

public class TicketCalender extends Fragment implements SlotSelectListner, View.OnClickListener {

    static final int MIN_DISTANCE = 50;
    private static final String tag = "MyCalendarActivity";
    private static final String dateTemplate = "MMMM yyyy";
    private final int[] m_cDaysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final String[] m_cDaysForWeeks = new String[]{"Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};
    private final String[] m_cMonths = {"Jan", "Feb", "Mar",
            "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};
    @SuppressWarnings("unused")
    @SuppressLint({"NewApi", "NewApi", "NewApi", "NewApi"})
    private final DateFormat dateFormatter = new DateFormat();
    public TextView m_cSelectedView;
    ArrayList<Integer> m_cPosn;
    ArrayList<Integer> m_cPreviousPos;
    ArrayList<String> m_cDayList;
    //    ArrayList<String> m_cMrngList, m_cNoonList, m_cEvengList;
    String m_cSelectedDate;
    String m_cTitle;
    int m_cConsulType;
    String m_cDoctorId;
    int m_cDate;
    String m_cMappingId;
    View view;
    MainActivity mainActivity;
    private ImageView iv_close;
    private float x1, x2;
    private int m_cRowId;
    private boolean SWIPED = false;
    private Calendar mCalendar;
    private TextView currentMonth;
    private ImageView prevMonth;

    //    CustomCalendarView m_cCalanderView;
    /*CalendarGridView m_cMrngGridSlots;
    TextView m_cMrngText;

    TextView m_cBtnSchedule;
    TextView m_cDoctorAdv;
    TextView m_cSuggestion;
    TextView m_cTimeOfSuggestion;*/
    private ImageView nextMonth;
    private CalendarGridView calendarView;
    private GridCellAdapter adapter;
    private Calendar m_cObjCalendar;
    private HashMap<String, String> m_cAbsents;
    private int m_cMonth;
    private int m_cYear;
    private int m_cToday_Date;
    private int m_cMonth_Now;
    private int m_cYear_Now;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ticket_calender, container, false);

        m_cObjCalendar = Calendar.getInstance(Locale.getDefault());
        m_cMonth = m_cObjCalendar.get(Calendar.MONTH);
        m_cYear = m_cObjCalendar.get(Calendar.YEAR);
        m_cAbsents = new HashMap<>();
        m_cAbsents.put("2016-04-14", "2016-04-14");
        m_cAbsents.put("2016-04-20", "2016-04-20");
        init(view);
        initlizationCalender(view);

        return view;

    }

    private void Todaysis() {
        m_cObjCalendar = Calendar.getInstance(Locale.getDefault());
        m_cToday_Date = m_cObjCalendar.get(Calendar.DAY_OF_MONTH);
        m_cMonth_Now = m_cObjCalendar.get(Calendar.MONTH);
        m_cYear_Now = m_cObjCalendar.get(Calendar.YEAR);
    }

    private void updateUIbyDate() {
        m_cDayList = new ArrayList<>();
        updateGrid();
    }


    private void updateGrid() {
        if (null != m_cDayList && m_cDayList.size() > 0) {
            SlotAdapter lMrngSlot = new SlotAdapter(mainActivity, m_cDayList, this);
            double size = m_cDayList.size() / 4.9;
//            double dp = (int)(360/this.getResources().getDisplayMetrics().density);
            final float scale = this.getResources().getDisplayMetrics().density;
            int pixels = (int) (79 * scale + 0.5f);
            double dp = pixels * (size + 0.5f);
            if (m_cDayList.size() <= 5) {
                dp = pixels * (size + 1);
            }
//            m_cMrngGridSlots.setLayoutParams(new LinearLayout.LayoutParams(CalendarGridView.AUTO_FIT, (int) dp));

//            m_cMrngGridSlots.setAdapter(lMrngSlot);
        }
        /*if(null != m_cNoonList && m_cNoonList.size() > 0) {
            SlotAdapter lNoonSlot = new SlotAdapter(this, m_cNoonList);
            int size = m_cNoonList.size()/3;
            int dp = (int)(320/this.getResources().getDisplayMetrics().density);
            dp = dp*size;
            m_cNoonGridSlots.setLayoutParams(new CalendarGridView.LayoutParams(CalendarGridView.AUTO_FIT, dp));
            m_cNoonGridSlots.setAdapter(lNoonSlot);
            m_cNoonGridSlots.setOnItemClickListener(this);
        }
        if(null != m_cEvengList && m_cEvengList.size() > 0) {
            SlotAdapter lEvengSlot = new SlotAdapter(this, m_cEvengList);
            int size = m_cEvengList.size()/3;
            int dp = (int)(320/this.getResources().getDisplayMetrics().density);
            dp = dp*size;
            m_cEvngGridSlots.setLayoutParams(new CalendarGridView.LayoutParams(CalendarGridView.AUTO_FIT, dp));
            m_cEvngGridSlots.setAdapter(lEvengSlot);
            m_cEvngGridSlots.setOnItemClickListener(this);
        }*/
    }

    @Override
    public void onSlotClickedListner(String pTimeSlot) {
        /*m_cObjAppoinmentTable.setStatus("2");
        String lStringDate = EURemediesMacros.getDateFormat(null, m_cSelectedDate, EURemediesMacros.DEFAULT_DATEFORMAT_YYYYMMDD, EURemediesMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_T);
        m_cObjAppoinmentTable.setAppointmentDate(lStringDate);
        m_cObjAppoinmentTable.setConsultationType(m_cConsulType + "");
        m_cObjAppoinmentTable.setFromTime(pTimeSlot);
        String[] FromDate = pTimeSlot.split(":");
        String ToDate;
        if(FromDate[1].contains("15")) {
            ToDate = FromDate[0]+":30";
        } else if(FromDate[1].contains("30")) {
            ToDate = FromDate[0]+":45";
        } else if(FromDate[1].contains("45")){
            int Number = Integer.valueOf(FromDate[0]);
            ToDate = (Number+1)+":00";
        } else {
            ToDate = FromDate[0]+":15";
        }
        m_cObjAppoinmentTable.setToTime(ToDate + ":00");
        m_cObjAppoinmentTable.setCaseID(m_cCaseId);
        m_cObjAppoinmentTable.setPatientID(m_cPatientID);*/
    }

    @Override
    public void onClick(View v) {
        Intent lIntent;
        String[] lSelectedDate;
        switch (v.getId()) {
            case R.id.prevMonth:
                Calendar lCal = Calendar.getInstance();
                int lCurrentMonth = lCal.get(Calendar.MONTH);
                int lCurrentYear = lCal.get(Calendar.YEAR);
                boolean lisAction = false;
                if (m_cYear > lCurrentYear) {
                    lisAction = true;
                } else if (m_cYear == lCurrentYear && m_cMonth > lCurrentMonth) {
                    lisAction = true;
                }
                if (lisAction) {
                    if (m_cMonth <= 0) {
                        m_cMonth = 11;
                        m_cYear--;
                    } else {
                        m_cMonth--;
                    }
                    setGridCellAdapterToDate(m_cMonth, m_cYear);
//                    m_cMrngGridSlots.setVisibility(View.GONE);
                }
                break;
            case R.id.nextMonth:

                if (m_cMonth >= 11) {
                    m_cMonth = 0;
                    m_cYear++;
                } else {
                    m_cMonth++;
                }
                setGridCellAdapterToDate(m_cMonth, m_cYear);
//                m_cMrngGridSlots.setVisibility(View.GONE);
                break;
            case R.id.currentMonth:
//                datePicker();
                break;
            case R.id.img_close:
                mainActivity.onBackPressed();
                break;
        }
    }

    private void init(View view) {
        //Initialize CustomCalendarView from layout
        iv_close = (ImageView) view.findViewById(R.id.img_close);
        iv_close.setOnClickListener(this);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        m_cSelectedDate = df.format(new Date());
        /*m_cMrngGridSlots = (CalendarGridView) findViewById(R.id.MORNING_GRID_SLOTS);

        m_cBtnSchedule = (TextView) findViewById(R.id.BTN_SCHEDULE);
        m_cTimeOfSuggestion = (TextView) findViewById(R.id.TXT_SCHDULE);
        m_cSuggestion = (TextView) findViewById(R.id.TXT_SUGGESTION);
        m_cDoctorAdv = (TextView) findViewById(R.id.TEXT_ADV_SCHDULE);

        m_cMrngText = (TextView) findViewById(R.id.MORNING_TEXT);

        m_cMrngText.setOnClickListener(this);
        m_cBtnSchedule.setOnClickListener(this);

        m_cTimeOfSuggestion.setText("Schedule a 15 mins " + m_cTitle + " call with the Doctor.");
        m_cSuggestion.setText("Select a available slot for " + m_cTitle + " Appointment");
*/
    }

    private void initlizationCalender(View view) {
        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setOnClickListener(this);
        currentMonth.setText(DateFormat.format(dateTemplate, m_cObjCalendar.getTime()));

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (CalendarGridView) view.findViewById(R.id.calendar);
        //NOTE: Uncomment when Swipe required.
//        calendarView.setOnTouchListener(this);

        // Initialised
        adapter = new GridCellAdapter(mainActivity, m_cMonth, m_cYear);
        calendarView.setAdapter(adapter);
    }


    private void setGridCellAdapterToDate(int month, int year) {
        adapter = new GridCellAdapter(mainActivity, month, year);
        m_cObjCalendar.set(year, month, 1);
        currentMonth.setText(DateFormat.format(dateTemplate, m_cObjCalendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    // Inner Class
    public class GridCellAdapter extends BaseAdapter implements View.OnClickListener {

        private static final String tag = "GridCellAdapter";
        private static final int DAY_OFFSET = 1;
        private final Context m_cContext;
        private final List<EachDate> m_cObjList;
        private final SimpleDateFormat m_cDateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
        private int m_cDaysInMonth;
        //		private int m_cSelectedDay;
        private TextView m_cGridText;
        private RelativeLayout m_cGridCell;

        public GridCellAdapter(Context context, int month, int year) {
            super();
            this.m_cContext = context;
            this.m_cObjList = new ArrayList<EachDate>();

            // Print Month
            printMonth(month, year);
        }

        @Override
        public int getCount() {
            return m_cObjList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return m_cObjList.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            LayoutInflater inflater = (LayoutInflater) m_cContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.screen_gridcell, parent, false);

            // Get a reference to the Day gridcell
            m_cGridText = (TextView) row.findViewById(R.id.CALENDAR_DAY_GRIDTEXT);
            m_cGridCell = (RelativeLayout) row.findViewById(R.id.CALENDAR_DAY_GRIDCELL);
//			LayoutParams lParams = new LayoutParams(lwidth, lwidth);
//			m_cGridText.setLayoutParams(lParams);
            m_cGridText.setOnClickListener(this);

            // ACCOUNT FOR SPACING

//			String[] day_color = m_cObjList.get(position).m_cDate.split("-");
            EachDate lEachDay = m_cObjList.get(position);
            String[] day = lEachDay.m_cDate.split("-");
//			m_cdayScreenDay = day[0];
//			M_cdayscreenMonth = day[1];
//			m_cdayscreenYear = day[2];

            // Set the Day GridCell
            m_cGridText.setText(day[0]);
            String lDate = m_cObjList.get(position).m_cDate;
            m_cGridText.setTag(lEachDay);
            m_cGridCell.setTag(lEachDay);

            if (lEachDay.m_cColor.equals("GREY")) {
                m_cGridText.setTextColor(Color.LTGRAY);
//                m_cGridText.setEnabled(false);
//                m_cGridText.setClickable(false);
//                m_cGridText.setFocusable(false);

//				m_cGridText.setVisibility(View.INVISIBLE);
//                m_cGridText.setFocusableInTouchMode(false);

            }
//            if (lEachDay.m_cColor.equals("WHITE")) {
//                m_cGridText.setTextColor(Color.BLACK);
//            }
//            if (lEachDay.m_cColor.equals("BLUE")) {
//                m_cGridText.setTextColor(Color.RED);
//            }
//            if (lEachDay.m_cColor.equals("YELLOW")) {
//                m_cGridText.setTextColor(Color.GREEN);
//            }

            if (lEachDay.m_cColor.equals("WHITE")) {
                m_cGridText.setTextColor(Color.BLACK);
                m_cGridText.setBackgroundColor(Color.WHITE);
            }
            if (lEachDay.m_cColor.equals("BLUE")) {
//                m_cGridText.setTextColor(Color.parseColor("#05CFB5"));
                m_cGridText.setTextColor(Color.WHITE);
                m_cGridText.setBackgroundResource(R.drawable.bluebg);
//                m_cGridCell.setBackground(getResources().getDrawable(R.drawable.circle_thin));
            }
            if (lEachDay.m_cColor.equals("YELLOW")) {
                m_cGridText.setTextColor(Color.RED);
                m_cGridText.setBackgroundColor(Color.WHITE);
                m_cGridText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.red_dot);
            }
            if (lEachDay.m_cColor.equals("RED")) {
                m_cGridText.setTextColor(Color.BLACK);
                m_cGridText.setBackgroundColor(Color.RED);
                m_cGridText.setPadding(0, 15, 0, 15);
            }

            return row;
        }

        public EachDate getGridItem(int pPos) {
            try {
                return m_cObjList.get(pPos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void setGridItem(int pPos, EachDate lItem) {
            m_cObjList.set(pPos, lItem);
        }

        public int getGridSize() {
            return m_cObjList.size();
        }


        @Override
        public void onClick(View v) {
            EachDate lTag = (EachDate) v.getTag();
            /*if (lTag.m_cColor.equals("YELLOW")) {
                m_cSelectedDate = lTag.m_cFormatDate;*/

            if (null != m_cSelectedView) {
                EachDate lEachDay = (EachDate) m_cSelectedView.getTag();
                if (lEachDay.m_cColor.equals("GREY")) {
                    m_cSelectedView.setTextColor(Color.LTGRAY);
                        /*m_cSelectedView.setEnabled(false);
                        m_cSelectedView.setClickable(false);
                        m_cSelectedView.setFocusable(false);
                        m_cSelectedView.setFocusableInTouchMode(false);*/
                }
                if (lEachDay.m_cColor.equals("WHITE")) {
                    m_cSelectedView.setTextColor(Color.BLACK);
                }
                if (lEachDay.m_cColor.equals("YELLOW")) {
                    m_cSelectedView.setTextColor(Color.BLACK);
                }
//                    m_cSelectedView.setBackgroundResource(R.drawable.cal_tile_selector);
                m_cSelectedView.setBackgroundResource(android.R.color.white);
                if (lEachDay.m_cColor.equals("BLUE")) {
                    m_cSelectedView.setBackgroundColor(Color.WHITE);
                    m_cSelectedView.setBackgroundResource(R.drawable.bluebg);
                }
            }
            if (!((EachDate) ((TextView) v).getTag()).m_cColor.equals("GREY")) {
                m_cSelectedView = (TextView) v;
                m_cSelectedView.setTextColor(Color.WHITE);
                m_cSelectedView.setBackgroundResource(R.drawable.selectdatebg);
//                m_cMrngGridSlots.setVisibility(View.VISIBLE);
                updateUIbyDate();
                mainActivity.exploreNearbyFragment.replaceTicketFragment(((EachDate) ((TextView) v).getTag()).m_cDate);
            }
            /*} else {
                int xOffset = Integer.parseInt(((TextView) v).getText().toString());
                int yOffset = Integer.parseInt(((TextView) v).getText().toString());
//                EURemediesMacros.showCustomAlert(m_cContext, xOffset, yOffset);
//                Toast.makeText(m_cContext, "Appointment not available..", Toast.LENGTH_SHORT).show();
            }*/
        }

        private void printMonth(int mm, int yy) {
            // The number of days to leave blank at
            // the start of this month.
            int lTrailingSpaces = 0;
            int lDaysInPrevMonth = 0;
            int lPrevMonth = 0;
            int lPrevYear = 0;
            int lNextMonth = 0;
            int lNextYear = 0;

            int lCurrentMonth = mm;
            m_cDaysInMonth = getNumberOfDaysOfMonth(lCurrentMonth);

            // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
            GregorianCalendar cal = new GregorianCalendar(yy, lCurrentMonth, 1);

            if (lCurrentMonth == 11) {
                lPrevMonth = lCurrentMonth - 1;
                lDaysInPrevMonth = getNumberOfDaysOfMonth(lPrevMonth);
                lNextMonth = 0;
                lPrevYear = yy;
                lNextYear = yy + 1;
            } else if (lCurrentMonth == 0) {
                lPrevMonth = 11;
                lPrevYear = yy - 1;
                lNextYear = yy;
                lDaysInPrevMonth = getNumberOfDaysOfMonth(lPrevMonth);
                lNextMonth = 1;
            } else {
                lPrevMonth = lCurrentMonth - 1;
                lNextMonth = lCurrentMonth + 1;
                lNextYear = yy;
                lPrevYear = yy;
                lDaysInPrevMonth = getNumberOfDaysOfMonth(lPrevMonth);
            }

            // Compute how much to leave before before the first day of the
            // month.
            // getDay() returns 0 for Sunday.
            int lCurrentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            lTrailingSpaces = lCurrentWeekDay;

            if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
                ++m_cDaysInMonth;
            }

            // Trailing Month days
            EachDate lDay = null;
            for (int i = 0; i < lTrailingSpaces; i++) {
                lDay = new EachDate();
                int lMonth = lPrevMonth + 1;
                String lMonthSt = null;
                if (lMonth <= 9) {
                    lMonthSt = "0" + lMonth;
                } else {
                    lMonthSt = "" + lMonth;
                }
                lDay.m_cFormatDate = String.format("%d-%s-%d", lPrevYear, lMonthSt, (lDaysInPrevMonth - lTrailingSpaces + DAY_OFFSET) + i);
                String lDate = String.valueOf((lDaysInPrevMonth - lTrailingSpaces + DAY_OFFSET) + i) + "-" + getMonthAsString(lPrevMonth) + "-" + lPrevYear;
                lDay.m_cDate = lDate;
                lDay.m_cColor = "GREY";
                m_cObjList.add(lDay);
                /*m_cObjList.add(String.valueOf((lDaysInPrevMonth
                        - lTrailingSpaces + DAY_OFFSET)
						+ i)
						+ "-GREY"
						+ "-"
						+ getMonthAsString(lPrevMonth)
						+ "-"
						+ lPrevYear);*/
            }
            Todaysis();
            for (int i = 1; i <= m_cDaysInMonth; i++) {
                lDay = new EachDate();
                int lMonth = lCurrentMonth + 1;
                String lMonthSt = null;
                if (lMonth <= 9) {
                    lMonthSt = "0" + lMonth;
                } else {
                    lMonthSt = "" + lMonth;
                }
                String lday = null;
                if (i <= 9) {
                    lday = "0" + i;
                } else {
                    lday = "" + i;
                }
                lDay.m_cFormatDate = String.format("%d-%s-%s", yy, lMonthSt, lday);
                String lDate = String.valueOf(i) + "-" + getMonthAsString(lCurrentMonth) + "-" + yy;

                lDay.m_cDate = lDate;
                if (i == m_cToday_Date && m_cMonth_Now == m_cMonth && m_cYear_Now == m_cYear) {
                    lDay.m_cColor = "BLUE";
                } else if (i < m_cToday_Date && m_cMonth_Now == m_cMonth && m_cYear_Now == m_cYear) {
                    lDay.m_cColor = "GREY";
                } else {
                    lDay.m_cColor = "WHITE";
                }

                /*if(null != m_cAbsents && m_cAbsents.containsKey(lDay.m_cFormatDate)) {
                    lDay.m_cColor = "YELLOW" ;
                }*/
                m_cObjList.add(lDay);
            }

            for (int i = 0; i < m_cObjList.size() % 7; i++) {
                lDay = new EachDate();
                int lMonth = lNextMonth + 1;
                String lMonthSt = null;
                if (lMonth <= 9) {
                    lMonthSt = "0" + lMonth;
                } else {
                    lMonthSt = "" + lMonth;
                }
                String lDate = String.valueOf(i + 1) + "-" + getMonthAsString(lNextMonth) + "-" + lNextYear;
                lDay.m_cFormatDate = String.format("%d-%s-%d", lNextYear, lMonthSt, i + 1);
                lDay.m_cDate = lDate;
                lDay.m_cColor = "GREY";
                m_cObjList.add(lDay);
            }

        }

        private String getMonthAsString(int i) {
            return m_cMonths[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return m_cDaysOfMonth[i];
        }
    }

    public class EachDate {
        public String m_cDate;
        public String m_cColor;
        public String m_cFormatDate;
    }


}


