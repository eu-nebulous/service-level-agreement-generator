let sls = [];
let transitions = [];
sls.push({
	slName: 1,
	slos: [
		{firstArgument:"RESPONSE_TIME",operator:"LESS_EQUAL_THAN",secondArgument:3, settlementPricePercentage: 0.05},
		{firstArgument:"AVAILABILITY",operator:"GREATER_EQUAL_THAN",secondArgument:99.9, settlementPricePercentage: 0.05}
	]
});

AddFirstSL();

async function createSLA(){
	let settlementData = transitions.pop();
	let data = {
		sls,
		transitions,
		settlement:{
			evaluationPeriod: settlementData.evaluationPeriod,
			settlementCount: settlementData.violationThreshold,
			concernedSL: settlementData.firstSl,
			settlementAction: "CANCEL"
		}
	};

	console.log(data);

	const response = await fetch("http://localhost:8081/create/sla",{
		method: "POST",
		headers: {
			"Content-Type": "application/json",
			"Access-Control-Allow-Origin": "cors"
		},
		body: JSON.stringify({data})
	});
	console.log(response);
}

function removeSL(sl){
	if(sls.length == 1){
		alert("At least one SL is required!");
		return;
	}

	sls = sls.slice(0, sl - 1).concat(sls.slice(sl));
	transitions	 = transitions = transitions.slice(0, sl - 1).concat(transitions.slice(sl));

	document.getElementById(`sl-${sl}`).remove();

	for(let i = sl; i <= sls.length; i++){
		let newSl = i;
		let oldSl = i + 1;
		console.log(i);
		let div = document.getElementById(`sl-${oldSl}`);
		div.id = `sl-${i}`;

		let h2 = document.getElementById(`sl-${oldSl}-h2`);
		h2.id = `sl-${newSl}-h2`;
		h2.innerHTML = `Service Level ${newSl}`;

		document.getElementById(`sl-${oldSl}-slos`).id = `sl-${newSl}-slos`;

		let evaluationThreshold = document.getElementById(`sl-${oldSl}-transition`);
		evaluationThreshold.id = `sl-${newSl}-transition`;
		evaluationThreshold.onchange = () => { updateTransition(newSl);};

		let evaluationPeriod =document.getElementById(`sl-${oldSl}-period`);
		evaluationPeriod.id = `sl-${newSl}-period`;
		evaluationPeriod.onchange = () => { updateTransition(newSl);};

		let remove = document.getElementById(`sl-${oldSl}-remove`);
		remove.id = `sl-${newSl}-remove`;
		remove.onclick = () => { removeSL(newSl);};	
		
		document.getElementById(`sl-${oldSl}-transition-label`).id = `sl-${newSl}-transition-label`;

		for(let j = 0; j < div.children[1].children.length; j++){
			let secArg = div.children[1].children[j].children[2];
			secArg.oninput = () => {updateSLO(secArg.value, newSl - 1, j);};
		}
	}

	document.getElementById(`sl-${sls.length}-transition-label`).innerHTML = "Violations to end SLA: ";

}

function addSLO(sl){
	let sloList = document.getElementById(`sl-${sl}-slos`);
	const slPercentage = document.getElementById(`slPercentage`);


	for(let i = 0; i < sls[sl - 2].slos.length; i++){
		let secArg;
		let sloData;
		if(isNaN(sls[sl - 2].slos[i].secondArgument))
			secArg = sls[sl - 2].slos[i].secondArgument;
		else{
			if(sls[sl - 2].slos[i].operator.includes("GREATER"))
				secArg = sls[sl - 2].slos[i].secondArgument * (1 - ( slPercentage.value / 100));
			else if(sls[sl - 2].slos[i].operator.includes("LESS"))
				secArg = sls[sl - 2].slos[i].secondArgument * (1 + ( slPercentage.value / 100));
			else 
				secArg = sls[sl - 2].slos[i].secondArgument;
		}
		sloData = {
			firstArgument: sls[sl - 2].slos[i].firstArgument,
			operator: sls[sl - 2].slos[i].operator,
			secondArgument: secArg 
		};
		sls[sl - 1].slos.push(sloData);

		sloList.innerHTML += `
			<div class="p-5 grid grid-cols-12 gap-2">
				<span class="mr-auto text-lg font-medium col-span-4">${sloData.firstArgument}</span>
				<span class="mr-auto text-lg font-medium col-span-4">${translateOperator(sloData.operator)}</span>
				<input oninput="updateSLO(this.value, ${sl - 1}, ${i})" aria-label="default input inline 4" placeholder="Value"class="disabled:bg-slate-100 disabled:cursor-not-allowed dark:disabled:bg-darkmode-800/50 dark:disabled:border-transparent [&[readonly]]:bg-slate-100 [&[readonly]]:cursor-not-allowed [&[readonly]]:dark:bg-darkmode-800/50 [&[readonly]]:dark:border-transparent transition duration-200 ease-in-out w-full text-sm border-slate-200 shadow-sm rounded-md placeholder:text-slate-400/90 focus:ring-4 focus:ring-primary focus:ring-opacity-20 focus:border-primary focus:border-opacity-40 dark:bg-darkmode-800 dark:border-transparent dark:focus:ring-slate-700 dark:focus:ring-opacity-50 dark:placeholder:text-slate-500/80 group-[.form-inline]:flex-1 group-[.input-group]:rounded-none group-[.input-group]:[&:not(:first-child)]:border-l-transparent group-[.input-group]:first:rounded-l group-[.input-group]:last:rounded-r group-[.input-group]:z-10 col-span-4" value="${sloData.secondArgument}">
			</div>`
			
	}
}

function updateSLO(value, sl, slo){
	console.log(sl);
	sls[sl].slos[slo].secondArgument = value;

	console.log(sls);
}

function updateTransition(sl){
	let evalPeriod = document.getElementById(`sl-${sl}-period`).value;
	let evalThreshold = document.getElementById(`sl-${sl}-transition`).value;
	transitions[sl - 1].evaluationPeriod = evalPeriod;
	transitions[sl - 1].evaluationThreshold = evalThreshold;

	console.log(transitions);
}

function translateOperator(op){
	let res;
	switch (op) {
		case "GREATER_EQUAL_THAN":
			res = ">=";
			break;
		case "LESS_EQUAL_THAN":
			res = "<=";
			break;
		case "LESS_THAN":
			res = "<=>";
			break;
		case "GREATER_THAN":
			res = ">";
			break;
		case "EQUALS":
			res = "=";
			break;
		case "NOT_EQUALS":
			res = "!=";
			break;
	}
	return res;
}

function addSL(){
	let sl = sls.length + 1;
	let div = document.createElement(`div`);
	
	// let trans;
	// if(sls.length == 1)
	// 	trans = {
	// 		firstlSL: sl - 1,
	// 		secondSl: sl,
	// 		evaluationPeriod: "PT1H",
	// 		violationThreshold: 4
	// 	};
	// else {
	trans = {
		firstSl: sl,
		secondSl: sl + 1,
		evaluationPeriod: transitions[transitions.length - 1].evaluationPeriod,
		violationThreshold: transitions[transitions.length - 1].violationThreshold
	}
	// }

	sls.push({
		slName: sl,
		slos:[]
	});

	transitions.push(trans);

	document.getElementById(`sl-${sl - 1}-transition-label`).innerHTML = "Violations to next SL:";

	div.innerHTML += 
	`<div id="sl-${sl}" class="preview-component intro-y box mt-5 p-5">
		<div class="flex flex-col items-center border-b border-slate-200/60 p-5 dark:border-darkmode-400 sm:flex-row">
			<h2 id="sl-${sl}-h2" class="mr-auto text-lg font-medium col-span-20">Service Level ${sl}</h2>
		</div>

		<div id="sl-${sl}-slos">
		</div>

		<div class="p-5 grid grid-cols-12 gap-2">
			<span id="sl-${sl}-transition-label" class="mr-auto text-lg font-medium col-span-6">Violations to end SLA: </span>
			<input onchange="updateTransition(${sl})" value="${trans.violationThreshold}" id="sl-${sl}-transition" min="1" max="1000000" data-tw-merge="" type="number" aria-label="default input inline 4" placeholder="Value"class="disabled:bg-slate-100 disabled:cursor-not-allowed dark:disabled:bg-darkmode-800/50 dark:disabled:border-transparent [&[readonly]]:bg-slate-100 [&[readonly]]:cursor-not-allowed [&[readonly]]:dark:bg-darkmode-800/50 [&[readonly]]:dark:border-transparent transition duration-200 ease-in-out w-full text-sm border-slate-200 shadow-sm rounded-md placeholder:text-slate-400/90 focus:ring-4 focus:ring-primary focus:ring-opacity-20 focus:border-primary focus:border-opacity-40 dark:bg-darkmode-800 dark:border-transparent dark:focus:ring-slate-700 dark:focus:ring-opacity-50 dark:placeholder:text-slate-500/80 group-[.form-inline]:flex-1 group-[.input-group]:rounded-none group-[.input-group]:[&:not(:first-child)]:border-l-transparent group-[.input-group]:first:rounded-l group-[.input-group]:last:rounded-r group-[.input-group]:z-10 col-span-6">
		</div>

		<div class="p-5 grid grid-cols-12 gap-2">
			<span class="mr-auto text-lg font-medium col-span-6">Evaluation Period: </span>
			<input onchange="updateTransition(${sl})" value="${trans.evaluationPeriod}" id="sl-${sl}-period" aria-label="default input inline 4" placeholder="Value"class="disabled:bg-slate-100 disabled:cursor-not-allowed dark:disabled:bg-darkmode-800/50 dark:disabled:border-transparent [&[readonly]]:bg-slate-100 [&[readonly]]:cursor-not-allowed [&[readonly]]:dark:bg-darkmode-800/50 [&[readonly]]:dark:border-transparent transition duration-200 ease-in-out w-full text-sm border-slate-200 shadow-sm rounded-md placeholder:text-slate-400/90 focus:ring-4 focus:ring-primary focus:ring-opacity-20 focus:border-primary focus:border-opacity-40 dark:bg-darkmode-800 dark:border-transparent dark:focus:ring-slate-700 dark:focus:ring-opacity-50 dark:placeholder:text-slate-500/80 group-[.form-inline]:flex-1 group-[.input-group]:rounded-none group-[.input-group]:[&:not(:first-child)]:border-l-transparent group-[.input-group]:first:rounded-l group-[.input-group]:last:rounded-r group-[.input-group]:z-10 col-span-6">
		</div>

		<button id="sl-${sl}-remove" onclick="removeSL(${sl})" id=data-tw-merge class="p-5 transition duration-200 border shadow-sm inline-flex items-center justify-center py-2 px-3 rounded-md font-medium cursor-pointer focus:ring-4 focus:ring-primary focus:ring-opacity-20 focus-visible:outline-none dark:focus:ring-slate-700 dark:focus:ring-opacity-50 [&:hover:not(:disabled)]:bg-opacity-90 [&:hover:not(:disabled)]:border-opacity-90 [&:not(button)]:text-center disabled:opacity-70 disabled:cursor-not-allowed bg-danger border-danger text-white dark:border-danger mb-2 mr-1 w-24 mb-2 mr-1 w-24">Delete SL</button>
			
		</div>`
	document.getElementById("sls").appendChild(div);

	console.log(transitions);
	addSLO(sl);
}

function AddFirstSL(){

	let sl = sls.length;
	let div = document.createElement(`div`);
	
	transitions.push({
		firstSl: 1,
		secondSl: 2,
		evaluationPeriod: "PT1H",
		violationThreshold: 4
	});

	div.innerHTML += 
	`<div id="sl-${sl}" class="preview-component intro-y box mt-5 p-5">
		<div class="flex flex-col items-center border-b border-slate-200/60 p-5 dark:border-darkmode-400 sm:flex-row">
			<h2 id="sl-${sl}-h2" class="mr-auto text-lg font-medium col-span-20">Service Level ${sl}</h2>
		</div>

		<div id="sl-${sl}-slos"></div>

		<div class="p-5 grid grid-cols-12 gap-2">
			<span id="sl-${sl}-transition-label" class="mr-auto text-lg font-medium col-span-6">Violations to end SLA: </span>
			<input onchange="updateTransition(${sl})" value="4" id="sl-${sl}-transition" min="1" max="1000000" data-tw-merge="" type="number" aria-label="default input inline 4" placeholder="Value"class="disabled:bg-slate-100 disabled:cursor-not-allowed dark:disabled:bg-darkmode-800/50 dark:disabled:border-transparent [&[readonly]]:bg-slate-100 [&[readonly]]:cursor-not-allowed [&[readonly]]:dark:bg-darkmode-800/50 [&[readonly]]:dark:border-transparent transition duration-200 ease-in-out w-full text-sm border-slate-200 shadow-sm rounded-md placeholder:text-slate-400/90 focus:ring-4 focus:ring-primary focus:ring-opacity-20 focus:border-primary focus:border-opacity-40 dark:bg-darkmode-800 dark:border-transparent dark:focus:ring-slate-700 dark:focus:ring-opacity-50 dark:placeholder:text-slate-500/80 group-[.form-inline]:flex-1 group-[.input-group]:rounded-none group-[.input-group]:[&:not(:first-child)]:border-l-transparent group-[.input-group]:first:rounded-l group-[.input-group]:last:rounded-r group-[.input-group]:z-10 col-span-6">
		</div>

		<div class="p-5 grid grid-cols-12 gap-2">
			<span class="mr-auto text-lg font-medium col-span-6">Evaluation Period: </span>
			<input onchange="updateTransition(${sl})" value="PT1H" id="sl-${sl}-period"  aria-label="default input inline 4" placeholder="Value"class="disabled:bg-slate-100 disabled:cursor-not-allowed dark:disabled:bg-darkmode-800/50 dark:disabled:border-transparent [&[readonly]]:bg-slate-100 [&[readonly]]:cursor-not-allowed [&[readonly]]:dark:bg-darkmode-800/50 [&[readonly]]:dark:border-transparent transition duration-200 ease-in-out w-full text-sm border-slate-200 shadow-sm rounded-md placeholder:text-slate-400/90 focus:ring-4 focus:ring-primary focus:ring-opacity-20 focus:border-primary focus:border-opacity-40 dark:bg-darkmode-800 dark:border-transparent dark:focus:ring-slate-700 dark:focus:ring-opacity-50 dark:placeholder:text-slate-500/80 group-[.form-inline]:flex-1 group-[.input-group]:rounded-none group-[.input-group]:[&:not(:first-child)]:border-l-transparent group-[.input-group]:first:rounded-l group-[.input-group]:last:rounded-r group-[.input-group]:z-10 col-span-6">
		</div>

		<button id="sl-${sl}-remove" onclick="removeSL(${sl})" id=data-tw-merge class="p-5 transition duration-200 border shadow-sm inline-flex items-center justify-center py-2 px-3 rounded-md font-medium cursor-pointer focus:ring-4 focus:ring-primary focus:ring-opacity-20 focus-visible:outline-none dark:focus:ring-slate-700 dark:focus:ring-opacity-50 [&:hover:not(:disabled)]:bg-opacity-90 [&:hover:not(:disabled)]:border-opacity-90 [&:not(button)]:text-center disabled:opacity-70 disabled:cursor-not-allowed bg-danger border-danger text-white dark:border-danger mb-2 mr-1 w-24 mb-2 mr-1 w-24">Delete SL</button>
			
	</div>`
	document.getElementById("sls").appendChild(div);


	let sloList = document.getElementById(`sl-${sl}-slos`);

	for(let i = 0; i < sls[0].slos.length; i++){
		let secArg;
		let sloData;
			secArg = sls[0].slos[i].secondArgument;
	
		sloData = {
			firstArgument: sls[0].slos[i].firstArgument,
			operator: sls[0].slos[i].operator,
			secondArgument: secArg 
		};

		sloList.innerHTML += `
		<div class="p-5 grid grid-cols-12 gap-2">
			<span class="mr-auto text-lg font-medium col-span-4">${sloData.firstArgument}</span>
			<span class="mr-auto text-lg font-medium col-span-4">${translateOperator(sloData.operator)}</span>
			<input oninput="updateSLO(this.value, ${sl - 1}, ${i})" aria-label="default input inline 4" placeholder="Value"class="disabled:bg-slate-100 disabled:cursor-not-allowed dark:disabled:bg-darkmode-800/50 dark:disabled:border-transparent [&[readonly]]:bg-slate-100 [&[readonly]]:cursor-not-allowed [&[readonly]]:dark:bg-darkmode-800/50 [&[readonly]]:dark:border-transparent transition duration-200 ease-in-out w-full text-sm border-slate-200 shadow-sm rounded-md placeholder:text-slate-400/90 focus:ring-4 focus:ring-primary focus:ring-opacity-20 focus:border-primary focus:border-opacity-40 dark:bg-darkmode-800 dark:border-transparent dark:focus:ring-slate-700 dark:focus:ring-opacity-50 dark:placeholder:text-slate-500/80 group-[.form-inline]:flex-1 group-[.input-group]:rounded-none group-[.input-group]:[&:not(:first-child)]:border-l-transparent group-[.input-group]:first:rounded-l group-[.input-group]:last:rounded-r group-[.input-group]:z-10 col-span-4" value="${sloData.secondArgument}">
		</div>`
		
	}
}