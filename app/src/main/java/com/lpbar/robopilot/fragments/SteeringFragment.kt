package com.lpbar.robopilot.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lpbar.robopilot.R
import com.lpbar.robopilot.activities.MainActivity
import com.lpbar.robopilot.services.NetworkServiceInterface
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.fragment_steering.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SteeringFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SteeringFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SteeringFragment : DialogFragment() {
    private var networkService: NetworkServiceInterface = MainActivity.networkService
    var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_steering, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        joystick.setOnMoveListener({ angle, strength ->
            if (strength == 0) {
                networkService.sendManualMotorAction(0.0, 0.0)
                return@setOnMoveListener
            }

            var angleNorm = 0.0
            var strengthNorm: Double = strength / 100.0
            if (angle < 180) {
                angleNorm = angle - 90.0
            } else {
                angleNorm = 270.0 - angle
                strengthNorm *= -1
            }

            networkService.sendManualMotorAction(angleNorm / 90.0, strengthNorm)
        }, 100)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                SteeringFragment().apply {
                    arguments = Bundle().apply { }
                }
    }
}
