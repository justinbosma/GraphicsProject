(
// The port Processing is listening to
n = NetAddr("127.0.0.1", 12000);
s.latency = 0.05;
s.boot;
TempoClock.default.tempo = 89/60;
)


(
SynthDef(\kick, {
	arg out = 0, gate = 1, freq, amp;
	var env, kickOsc, kickOut, clickOsc, clickEnv, clickOut;
	env = EnvGen.kr(Env.perc(0.00, 0.5), gate, doneAction: 2);
	kickOsc = SinOsc.ar(freq: freq, mul: amp);
	kickOut = (kickOsc*env);

	clickEnv = EnvGen.kr(Env.perc(0, 0.02));
	clickOsc = (LPF.ar(WhiteNoise.ar(1), 500));
	clickOut = (clickOsc*clickEnv);
	Out.ar(0,
		Pan2.ar(kickOut + clickOut, 0));
	};
).add;
)

(
SynthDef(\hihat, {
	arg out = 0, gate = 1, freq, amp;
	var env, osc;
	env = EnvGen.kr(Env.perc(), gate, doneAction: 2);
	osc = RHPF.ar(WhiteNoise.ar(mul: amp*0.01), freq: freq);
	out = (osc*env);

	Out.ar(0,
		Pan2.ar(out, 0));
	};
).add;
)



(
	SynthDef(\drone, {|freq, gate = 1, amp, cut = 350|
		var env, out, osc;
	//env = EnvGen.kr(Env.adsr(0.75, 0.1, 0.5, 0.0), doneAction: 2);
	env = EnvGen.kr(Env.new([0, 0.25, 0.5, 1, 0.5, 0.25, 0.1, 0], [0.3, 0.3, 3.0, 3.4, 2.0, 2.5, 0.5]),doneAction: 2);
	osc = RLPF.ar(Saw.ar(freq: freq, mul: 0.5) + Saw.ar(freq: freq*(1/2), mul: 0.5) + Saw.ar(freq: freq*(1/3), mul: 0.5) + Saw.ar(freq: freq*(1/4), mul: 0.5) + Saw.ar(freq: freq*(1/5), mul: 0.5), freq: cut);
		out = osc*env;
		Out.ar(0,
				Pan2.ar(out, 0));
		};
	).add;
)

(
	SynthDef(\bass, {|freq, gate = 1, amp|
		var env, out, osc;
	env = EnvGen.kr(Env.new([0, 0.25, 0.5, 1, 0.5, 0.25, 0.1, 0], [0.3, 0.3, 0.3, 0.5, 0.2, 0.5, 0.5]),doneAction: 2);
		osc = SinOsc.ar(freq: freq, mul: amp);
		out = osc*env;
		Out.ar(0,
				Pan2.ar(out, 0));
		};
	).add;
)


(
	SynthDef(\mallet, {|freq, amp, gate = 1|
		var env, out, osc;
	env = EnvGen.kr(Env.perc(),doneAction: 2);
	osc = RLPF.ar(SinOsc.ar(freq: freq, mul: amp), freq: 350);
		out = osc*env;
		Out.ar(0,
				Pan2.ar(out, 0));
		};
	).add;
)

(
// Listen to the events from my sequencer
Event.addEventType(\ev1, {
    // Play a sound
    Synth(\kick, [freq: ~freq, amp: ~amp]);
    // Tell Processing to visualize my sound
    n.sendMsg("/kick", ~freq, ~amp, "kick");
});
)

(
// Listen to the events from my sequencer
Event.addEventType(\ev2, {
    // Play a sound
    Synth(\bass, [freq: ~freq, amp: ~amp]);
    // Tell Processing to visualize my sound
    n.sendMsg("/bass", ~freq, ~amp, "bass");
});
)

(
// Listen to the events from my sequencer
Event.addEventType(\ev3, {
    // Play a sound
    Synth(\drone, [freq: ~freq, amp: ~amp]);
    // Tell Processing to visualize my sound
    n.sendMsg("/boohp", ~freq, ~amp, "boohp");
});
)

(
// Listen to the events from my sequencer
Event.addEventType(\ev4, {
    // Play a sound
    Synth(\hihat, [freq: ~freq, amp: ~amp]);
    // Tell Processing to visualize my sound
    n.sendMsg("/hihat", ~freq, ~amp, "hihat");
});
)

(
// Listen to the events from my sequencer
Event.addEventType(\ev5, {
    // Play a sound
    Synth(\mallet, [freq: ~freq, amp: ~amp]);
    // Tell Processing to visualize my sound
	n.sendMsg("/mallet", ~freq, ~amp, "mallet");
});
)


(
p = Pbind(
	\type, \ev1,
	\freq, 60.0,
	\amp, Pseq( #[1.0, 0.5, 0.25, 0.125], inf),
	\dur, Pseq( #[1.0, 2.0, 2.0, 3.0], inf);
).play;
)
p.stop;



(
p.stream = Pbind(
	\type, \ev1,
	\freq, Pseq( #[60.0, 200.0, 60.0, 100.0, 75.0], inf),
	\amp, 1.0,
	\dur, Pseq( #[1, 0.5, 1, 0.5, 1.0], inf);
).asStream;
)

(
b = Pbind(
	\type, \ev2,
	\freq, Pseq( [110, 110, 110], inf),
	\dur, Pseq( #[0.25, 0.75, 3.0], inf),
	\amp, 1.0
).play;
)
b.stop;

(
c = Pbind(
	\type, \ev3,
	\freq, Pseq([440], inf),
	//\amp, 0.7,
	\dur, Pseq( #[8.0], inf);
).play;
)
c.stop;
(
c.stream = Pbind(
	\type, \ev3,
	\freq, Pseq( [440, 440*(35/18)], inf),
	\amp, 0.7,
	\dur, Pseq( #[8, 8], inf);
).asStream;
)
c.stop;

(
h = Pbind(
	\type, \ev4,
	\freq, 880.0,
	\amp, 0.75,
	\dur, Pseq( #[0.025], inf);
).play;
)
h.stop;
p.stop;

(
h.stream = Pbind(
	\type, \ev4,
	\freq, Pseq( #[10.0, 75.0, 100.0, 200.0], inf),
	\amp, Pseq( #[0.3, 0.05, 0.07, 0.125], inf),
	\dur, Pseq( #[1.0], inf);
).asStream;
)
h.play;


(
h.stream = Pbind(
	\type, \ev4,
	\freq, 1000.0,
	\amp, Pseq( #[0.1, 0.01, 0.001], inf),
	\dur, Pseq( #[0.25], inf);
).asStream;
)

(
m = Pbind(
	\type, \ev5,
	\freq, Pseq([880, 440*(15/13), 440*(16/9), 440*(7/5)], inf),
	\amp, 0.7,
	\dur, Pseq( #[1, 1, 1, 1, 1], inf);
).play
)
m.stop;
(
m.stream = Pbind(
	\type, \ev5,
	\freq, Pseq([880, 440*(35/18), 880*(16/9), 440*(7/5)], inf),
	\amp, 0.7,
	\dur, Pseq( #[1, 1, 1, 1], inf);
).asStream;
)


m.play;
p.play;
p.stop;
b.play;
b.stop;
c.play;
c.stop;
h.play(quant: 4);
h.stop;


(
p.play;
h.play(quant: [16, 0, 0]);
b.play(quant: [4, 0, 0]);
c.play(quant: [8, 0, 0]);
)
