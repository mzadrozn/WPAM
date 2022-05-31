package com.example.wpamapp;

import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.example.wpamapp.custom.MyMarkerView;
import com.example.wpamapp.notimportant.DemoBase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SupplaChartActivity extends DemoBase implements
        OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {

    private LineChart chart;
    EditText etDateFrom;
    EditText etDateTo;
    long epoch_from;
    long epoch_to;
    Spinner dropdown;
    String dropdown_type = "fae";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_suppla_chart);

        setTitle("Total used power");

        etDateFrom = findViewById(R.id.etDateFrom);
        etDateTo = findViewById(R.id.etDateTo);
        dropdown = findViewById(R.id.dropdown);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.power_array, android.R.layout.simple_spinner_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SupplaChartActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date_str = dayOfMonth+"/"+month+"/"+year;
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
                        try {
                            Date date = df.parse(date_str);
                            long epoch = date.getTime();
                            epoch_from = epoch;
                            etDateFrom.setText(date_str);
                            if( epoch_to > epoch_from && epoch_to != 0 && epoch_from != 0){
                                SupplaApiRequest wpamCall = new SupplaApiRequest(epoch_from, epoch_to);
                                ExecutorService service = Executors.newFixedThreadPool(1);
                                Future<String> result = service.submit(wpamCall);
                                String result_string = result.get();
                                final JSONArray arr = new JSONArray(result_string);
                                final int n = arr.length();
                                setData(n, arr, epoch_from, epoch_to, dropdown_type);
                            }
                            chart.animateX(1500);
                        } catch (Exception e) {
                            e.printStackTrace();
                            etDateFrom.setText(date_str);
                        }
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        etDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SupplaChartActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date_str = dayOfMonth+"/"+month+"/"+year;
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
                        try {
                            Date date = df.parse(date_str);
                            long epoch = date.getTime();
                            epoch_to = epoch;
                            etDateTo.setText(date_str);
                            if( epoch_to > epoch_from && epoch_to != 0 && epoch_from != 0){
                                SupplaApiRequest wpamCall = new SupplaApiRequest(epoch_from, epoch_to);
                                ExecutorService service = Executors.newFixedThreadPool(1);
                                Future<String> result = service.submit(wpamCall);
                                String result_string = result.get();
                                final JSONArray arr = new JSONArray(result_string);
                                final int n = arr.length();
                                setData(n, arr, epoch_from, epoch_to, dropdown_type);
                            }
                            chart.animateX(1500);
                        } catch (Exception e) {
                            e.printStackTrace();
                            etDateTo.setText(date_str);
                        }
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        {   // // Chart Style // //
            chart = findViewById(R.id.chart1);

            // background color
            chart.setBackgroundColor(Color.WHITE);

            // disable description text
            chart.getDescription().setEnabled(false);

            // enable touch gestures
            chart.setTouchEnabled(true);

            // set listeners
            chart.setOnChartValueSelectedListener(this);
            chart.setDrawGridBackground(false);

            // create marker to display box when values are selected
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

            // Set the marker to the chart
            mv.setChartView(chart);
            chart.setMarker(mv);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);

            // force pinch zoom along both axis
            chart.setPinchZoom(true);
        }

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
            xAxis.setValueFormatter(new LineChartXAxisValueFormatter());
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

        }

    }

    private void setData(int count, JSONArray array, long epoch_from, long epoch_to, String type) {

        ArrayList<Entry> values_phase1 = new ArrayList<>();
        ArrayList<Entry> values_phase2 = new ArrayList<>();
        ArrayList<Entry> values_phase3 = new ArrayList<>();
        ArrayList<Entry> values_sum = new ArrayList<>();

        try {
            for(int i = count - 1; i >= 0; --i) {

                final JSONObject row = array.getJSONObject(i);
                int fae1 = row.getInt("phase1_" + type);
                int fae2 = row.getInt("phase2_" + type);
                int fae3 = row.getInt("phase3_" + type);
                int fae_sum = fae1 + fae2 + fae3;

                int time = row.getInt("date_timestamp");
                if(time > epoch_from / 1000 && time <= epoch_to / 1000){
                    values_phase1.add(new Entry(time,fae1));
                    values_phase2.add(new Entry(time,fae2));
                    values_phase3.add(new Entry(time,fae3));
                    values_sum.add(new Entry(time,fae_sum));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        LineDataSet set1;
        LineDataSet set2;
        LineDataSet set3;
        LineDataSet set_sum;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            set3 = (LineDataSet) chart.getData().getDataSetByIndex(2);
            set_sum = (LineDataSet) chart.getData().getDataSetByIndex(3);
            set1.setValues(values_phase1);
            set2.setValues(values_phase2);
            set3.setValues(values_phase3);
            set_sum.setValues(values_sum);

            set1.setLabel("Phase1 " + type);
            set2.setLabel("Phase2 " + type);
            set3.setLabel("Phase3 " + type);
            set_sum.setLabel("Sum " + type);

            set1.notifyDataSetChanged();
            set2.notifyDataSetChanged();
            set3.notifyDataSetChanged();
            set_sum.notifyDataSetChanged();

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values_phase1, "Phase1 " + type);
            set2 = new LineDataSet(values_phase2, "Phase2 " + type);
            set3 = new LineDataSet(values_phase3, "Phase3 " + type);
            set_sum = new LineDataSet(values_sum, "Sum " + type);

            set1.setDrawIcons(false);
            set2.setDrawIcons(false);
            set3.setDrawIcons(false);
            set_sum.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);
            set2.enableDashedLine(10f, 5f, 0f);
            set3.enableDashedLine(10f, 5f, 0f);
            set_sum.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.GREEN);
            set1.setCircleColor(Color.GREEN);
            set2.setColor(Color.RED);
            set2.setCircleColor(Color.RED);
            set3.setColor(Color.BLUE);
            set3.setCircleColor(Color.BLUE);
            set_sum.setColor(Color.BLACK);
            set_sum.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set2.setLineWidth(1f);
            set2.setCircleRadius(3f);
            set3.setLineWidth(1f);
            set3.setCircleRadius(3f);
            set_sum.setLineWidth(1f);
            set_sum.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);
            set2.setDrawCircleHole(false);
            set3.setDrawCircleHole(false);
            set_sum.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set2.setFormLineWidth(1f);
            set2.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set2.setFormSize(15.f);
            set3.setFormLineWidth(1f);
            set3.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set3.setFormSize(15.f);
            set_sum.setFormLineWidth(1f);
            set_sum.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set_sum.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);
            set2.setValueTextSize(9f);
            set3.setValueTextSize(9f);
            set_sum.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set2.enableDashedHighlightLine(10f, 5f, 0f);
            set3.enableDashedHighlightLine(10f, 5f, 0f);
            set_sum.enableDashedHighlightLine(10f, 5f, 0f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets
            dataSets.add(set2);
            dataSets.add(set3);
            dataSets.add(set_sum);

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suppla_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                chart.invalidate();
                break;
            }
            case R.id.actionToggleIcons: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawIcons(!set.isDrawIconsEnabled());
                }

                chart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if(chart.getData() != null) {
                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
                    chart.invalidate();
                }
                break;
            }
            case R.id.actionToggleFilled: {

                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionToggleCubic: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            :  LineDataSet.Mode.CUBIC_BEZIER);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionToggleStepped: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.STEPPED
                            ? LineDataSet.Mode.LINEAR
                            :  LineDataSet.Mode.STEPPED);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionToggleHorizontalCubic: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            :  LineDataSet.Mode.HORIZONTAL_BEZIER);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (chart.isPinchZoomEnabled())
                    chart.setPinchZoom(false);
                else
                    chart.setPinchZoom(true);

                chart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
                chart.notifyDataSetChanged();
                break;
            }
            case R.id.animateX: {
                chart.animateX(2000);
                break;
            }
            case R.id.animateY: {
                chart.animateY(2000, Easing.EaseInCubic);
                break;
            }
            case R.id.animateXY: {
                chart.animateXY(2000, 2000);
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(chart);
                }
                break;
            }
        }
        return true;
    }


    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "LineChartActivity1");
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOW HIGH", "low: " + chart.getLowestVisibleX() + ", high: " + chart.getHighestVisibleX());
        Log.i("MIN MAX", "xMin: " + chart.getXChartMin() + ", xMax: " + chart.getXChartMax() + ", yMin: " + chart.getYChartMin() + ", yMax: " + chart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        dropdown_type = dropdown.getSelectedItem().toString();

        try {
            if( epoch_to > epoch_from && epoch_to != 0 && epoch_from != 0){
                SupplaApiRequest wpamCall = new SupplaApiRequest(epoch_from, epoch_to);
                ExecutorService service = Executors.newFixedThreadPool(1);
                Future<String> result = service.submit(wpamCall);
                String result_string = result.get();
                final JSONArray arr = new JSONArray(result_string);
                final int n = arr.length();
                setData(n, arr, epoch_from, epoch_to, dropdown_type);
            }
            chart.animateX(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        return;
    }
}

