package mx.grupohi.almacenes.almacensao;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImprimirEntradaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImprimirEntradaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImprimirEntradaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;
    ListView mViajesList;
    AdaptadorInicio adapter;

    public ImprimirEntradaFragment() {
        // Required empty public constructor
    }
    public static ImprimirEntradaFragment newInstance(String param1, String param2) {
        ImprimirEntradaFragment fragment = new ImprimirEntradaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context con = container.getContext();
        View root = inflater.inflate(R.layout.fragment_imprimir_entrada, container, false);
        mViajesList = (ListView) root.findViewById(R.id.list);
        adapter = new AdaptadorInicio(getActivity(), EntradaDetalle.getEntradas(getContext()));
        mViajesList.setAdapter(adapter);

        mViajesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EntradaDetalle viajeActual = adapter.getItem(position);
                System.out.println("press: "+position);
               /* Intent intent = new Intent(con, SuccessDestinoActivity.class);
                intent.putExtra("idViaje", viajeActual.idViaje);
                intent.putExtra("list", 1);
                startActivity(intent);*/
            }
        });

        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static ImprimirEntradaFragment newInstance() {
        ImprimirEntradaFragment fragment = new ImprimirEntradaFragment();
        // Setup parámetros
        return fragment;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
